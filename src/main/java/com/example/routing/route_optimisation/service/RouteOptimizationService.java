package com.example.routing.route_optimisation.service;

import com.example.routing.route_optimisation.dto.RouteOptimizationRequest;
import com.example.routing.route_optimisation.dto.RouteOptimizationResponse;
import com.example.routing.route_optimisation.model.Location;
import com.example.routing.route_optimisation.model.Order;
import com.example.routing.route_optimisation.model.Truck;
import com.example.routing.route_optimisation.service.DistanceMatrixService;
import com.google.ortools.Loader;
import com.google.ortools.constraintsolver.Assignment;
import com.google.ortools.constraintsolver.FirstSolutionStrategy;
import com.google.ortools.constraintsolver.RoutingIndexManager;
import com.google.ortools.constraintsolver.RoutingModel;
import com.google.ortools.constraintsolver.RoutingSearchParameters;
import com.google.ortools.constraintsolver.main;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Service
public class RouteOptimizationService {

    private static final Logger logger = Logger.getLogger(RouteOptimizationService.class.getName());
    private final DistanceMatrixService distanceMatrixService;

    static {
        Loader.loadNativeLibraries();
    }

    public RouteOptimizationService(DistanceMatrixService distanceMatrixService) {
        this.distanceMatrixService = distanceMatrixService;
    }

    public RouteOptimizationResponse optimize(RouteOptimizationRequest request) {
        List<Order> orders = request.getOrders();
        List<Truck> trucks = request.getTrucks();
        Location depot = request.getDepot();

        int vehicleNumber = trucks.size();
        int depotIndex = 0;

        List<Location> allLocations = new ArrayList<>();
        allLocations.add(depot);
        for (Order order : orders) {
            allLocations.add(order.getLocation());
        }

        // Initialize/Cache Distance Matrix
        distanceMatrixService.initMatrix(allLocations);

        RoutingIndexManager manager = new RoutingIndexManager(allLocations.size(), vehicleNumber, depotIndex);
        RoutingModel routing = new RoutingModel(manager);

        // 1. Define Weight Dimension
        final int weightCallbackIndex = routing.registerUnaryTransitCallback((long fromIndex) -> {
            int fromNode = manager.indexToNode(fromIndex);
            if (fromNode == 0)
                return 0;
            return orders.get(fromNode - 1).getWeight();
        });
        long[] weightCapacities = trucks.stream().mapToLong(Truck::getWeightCapacity).toArray();
        routing.addDimensionWithVehicleCapacity(weightCallbackIndex, 0, weightCapacities, true, "Weight");

        // 2. Define Length Dimension
        final int lengthCallbackIndex = routing.registerUnaryTransitCallback((long fromIndex) -> {
            int fromNode = manager.indexToNode(fromIndex);
            if (fromNode == 0)
                return 0;
            return orders.get(fromNode - 1).getLength();
        });
        long[] lengthCapacities = trucks.stream().mapToLong(Truck::getLengthCapacity).toArray();
        routing.addDimensionWithVehicleCapacity(lengthCallbackIndex, 0, lengthCapacities, true, "Length");

        // 3. Define Width Dimension
        final int widthCallbackIndex = routing.registerUnaryTransitCallback((long fromIndex) -> {
            int fromNode = manager.indexToNode(fromIndex);
            if (fromNode == 0)
                return 0;
            return orders.get(fromNode - 1).getWidth();
        });
        long[] widthCapacities = trucks.stream().mapToLong(Truck::getWidthCapacity).toArray();
        routing.addDimensionWithVehicleCapacity(widthCallbackIndex, 0, widthCapacities, true, "Width");

        // 4. Define Height Dimension
        final int heightCallbackIndex = routing.registerUnaryTransitCallback((long fromIndex) -> {
            int fromNode = manager.indexToNode(fromIndex);
            if (fromNode == 0)
                return 0;
            return orders.get(fromNode - 1).getHeight();
        });
        long[] heightCapacities = trucks.stream().mapToLong(Truck::getHeightCapacity).toArray();
        routing.addDimensionWithVehicleCapacity(heightCallbackIndex, 0, heightCapacities, true, "Height");

        // 5. Define Stops Dimension (for max stops constraint)
        final int stopsCallbackIndex = routing.registerUnaryTransitCallback((long fromIndex) -> {
            int fromNode = manager.indexToNode(fromIndex);
            return fromNode == 0 ? 0 : 1;
        });
        long[] stopsCapacities = trucks.stream()
                .mapToLong(t -> t.getMaxStops() > 0 ? t.getMaxStops() : Integer.MAX_VALUE)
                .toArray();
        routing.addDimensionWithVehicleCapacity(stopsCallbackIndex, 0, stopsCapacities, true, "Stops");
        com.google.ortools.constraintsolver.RoutingDimension stopsDimension = routing.getMutableDimension("Stops");
        // Add a soft Global Span Cost to balance the number of stops across vehicles.
        // A coefficient of 5000 means for every unit of difference between max and min
        // stops,
        // add 5000 to the objective function cost. This encourages fairness.
        // stopsDimension.setGlobalSpanCostCoefficient(5000); // REVERTED: User prefers
        // minimizing total cost/distance over fairness.

        // 6. Cost Function & Time Dimension
        // We need separate time callbacks if speeds differ.
        // And separate cost callbacks.

        // Array containing the callback index for each vehicle
        int[] vehicleTimeCallbackIndices = new int[vehicleNumber];

        for (int i = 0; i < vehicleNumber; i++) {
            Truck truck = trucks.get(i);
            final double speedKmH = truck.getSpeedKmH() > 0 ? truck.getSpeedKmH() : 40.0; // Default 40 if 0

            // Time Callback for this specific truck
            final int timeCallbackIndex = routing.registerTransitCallback((long fromIndex, long toIndex) -> {
                int fromNode = manager.indexToNode(fromIndex);
                int toNode = manager.indexToNode(toIndex);

                double distanceKm = distanceMatrixService.getDistance(allLocations.get(fromNode),
                        allLocations.get(toNode));
                long travelTimeMinutes = (long) ((distanceKm / speedKmH) * 60);

                long serviceTime = 0;
                if (fromNode != 0) {
                    serviceTime = orders.get(fromNode - 1).getServiceTime();
                }
                return travelTimeMinutes + serviceTime;
            });
            vehicleTimeCallbackIndices[i] = timeCallbackIndex;

            // Cost Callback for this specific truck
            // Cost = (Distance * costPerKm) + (Time * costPerHour) + FixedCost (handled
            // separately)
            final int costCallbackIndex = routing.registerTransitCallback((long fromIndex, long toIndex) -> {
                int fromNode = manager.indexToNode(fromIndex);
                int toNode = manager.indexToNode(toIndex);

                double distanceKm = distanceMatrixService.getDistance(allLocations.get(fromNode),
                        allLocations.get(toNode));

                // Re-calculate time or reuse? Re-calc is safer here since we are inside a
                // lambda
                long travelTimeMinutes = (long) ((distanceKm / speedKmH) * 60);

                double cost = (distanceKm * truck.getCostPerKm())
                        + ((travelTimeMinutes / 60.0) * truck.getCostPerHour());
                return (long) cost;
            });

            routing.setArcCostEvaluatorOfVehicle(costCallbackIndex, i);
            routing.setFixedCostOfVehicle((long) truck.getFixedCost(), i);
        }

        // Add Time Dimension using the vector of callbacks
        long horizon = 24 * 60; // 24 hours
        routing.addDimensionWithVehicleTransits(vehicleTimeCallbackIndices, horizon, horizon, false, "Time");

        // Add Time Window Constraints for Orders
        // ... (Implement if needed, or rely on Time dimension bounds if defined in
        // request)

        // 7. Allow dropping orders (Disjunction)
        // If an order cannot be visited, we drop it with a high penalty.
        // This ensures a solution is always found even if constraints (like capacity)
        // are violated.
        long penalty = 10_000_000; // High penalty to prioritize visiting over dropping
        for (int i = 0; i < orders.size(); ++i) {
            long index = manager.nodeToIndex(i + 1);
            routing.addDisjunction(new long[] { index }, penalty);
        }

        // Solve
        RoutingSearchParameters searchParameters = main.defaultRoutingSearchParameters()
                .toBuilder()
                .setFirstSolutionStrategy(FirstSolutionStrategy.Value.PATH_CHEAPEST_ARC)
                .build();

        Assignment solution = routing.solveWithParameters(searchParameters);

        return constructResponse(routing, manager, solution, trucks, orders, allLocations);
    }

    private RouteOptimizationResponse constructResponse(RoutingModel routing, RoutingIndexManager manager,
            Assignment solution, List<Truck> trucks,
            List<Order> orders, List<Location> allLocations) {
        RouteOptimizationResponse response = new RouteOptimizationResponse();
        List<RouteOptimizationResponse.VehicleRoute> vehicleRoutes = new ArrayList<>();
        double totalDistance = 0;
        long totalTime = 0;
        double totalCost = 0;

        List<String> visitedOrderIds = new ArrayList<>();

        if (solution != null) {
            for (int i = 0; i < trucks.size(); ++i) {
                long index = routing.start(i);
                RouteOptimizationResponse.VehicleRoute route = new RouteOptimizationResponse.VehicleRoute();
                route.setTruckId(trucks.get(i).getId());
                List<String> orderSequence = new ArrayList<>();
                double routeDistance = 0;

                while (!routing.isEnd(index)) {
                    int nodeIndex = manager.indexToNode(index);
                    if (nodeIndex != 0) {
                        String orderId = orders.get(nodeIndex - 1).getId();
                        orderSequence.add(orderId);
                        visitedOrderIds.add(orderId);
                    }
                    long previousIndex = index;
                    index = solution.value(routing.nextVar(index));
                    double segmentDistance = distanceMatrixService.getDistance(
                            allLocations.get(manager.indexToNode(previousIndex)),
                            allLocations.get(manager.indexToNode(index)));
                    routeDistance += segmentDistance;
                }
                route.setOrderSequence(orderSequence);
                route.setDistance(routeDistance);
                // Calculate time based on distance and speed (approximation for response)
                // Note: accurate time should come from the Time dimension if we want to include
                // wait times and windows.
                // For now, we recalculate travel time + service times.
                double speed = trucks.get(i).getSpeedKmH() > 0 ? trucks.get(i).getSpeedKmH() : 40.0;
                long routeTime = (long) ((routeDistance / speed) * 60);
                // Add service times for visited orders
                for (String orderId : orderSequence) {
                    // Find order by ID (suboptimal but fine for MVP)
                    Order o = orders.stream().filter(or -> or.getId().equals(orderId)).findFirst().orElse(null);
                    if (o != null) {
                        routeTime += o.getServiceTime();
                    }
                }
                route.setTime(routeTime);

                // Calculate Cost
                Truck truck = trucks.get(i);
                double cost = (routeDistance * truck.getCostPerKm())
                        + ((routeTime / 60.0) * truck.getCostPerHour())
                        + (orderSequence.isEmpty() ? 0 : truck.getFixedCost());

                route.setCost(cost);

                vehicleRoutes.add(route);
                totalDistance += routeDistance;
                totalTime += routeTime;
                totalCost += cost;
            }
        }

        // Identify unassigned orders
        List<String> unassignedOrders = new ArrayList<>();
        for (Order order : orders) {
            if (!visitedOrderIds.contains(order.getId())) {
                unassignedOrders.add(order.getId());
            }
        }

        response.setRoutes(vehicleRoutes);
        response.setUnassignedOrders(unassignedOrders);
        response.setTotalDistance(totalDistance);
        response.setTotalTime(totalTime);
        response.setTotalCost(totalCost);
        return response;
    }
}
