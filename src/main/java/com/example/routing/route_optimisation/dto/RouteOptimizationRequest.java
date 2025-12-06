package com.example.routing.route_optimisation.dto;

import com.example.routing.route_optimisation.model.Order;
import com.example.routing.route_optimisation.model.Truck;
import com.example.routing.route_optimisation.model.Location;

import java.util.List;

public class RouteOptimizationRequest {
    private List<Order> orders;
    private List<Truck> trucks;
    private Location depot; // Fallback if trucks don't have individual start locations

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public List<Truck> getTrucks() {
        return trucks;
    }

    public void setTrucks(List<Truck> trucks) {
        this.trucks = trucks;
    }

    public Location getDepot() {
        return depot;
    }

    public void setDepot(Location depot) {
        this.depot = depot;
    }
}
