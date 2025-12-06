package com.example.routing.route_optimisation.dto;

import java.util.List;

public class RouteOptimizationResponse {
    private List<VehicleRoute> routes;
    private List<String> unassignedOrders;
    private double totalDistance;
    private long totalTime;
    private double totalCost;

    public List<VehicleRoute> getRoutes() {
        return routes;
    }

    public void setRoutes(List<VehicleRoute> routes) {
        this.routes = routes;
    }

    public List<String> getUnassignedOrders() {
        return unassignedOrders;
    }

    public void setUnassignedOrders(List<String> unassignedOrders) {
        this.unassignedOrders = unassignedOrders;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public static class VehicleRoute {
        private String truckId;
        private List<String> orderSequence; // IDs of orders, starting/ending with depot?
        private double distance; // in km
        private long time; // in minutes
        private double cost;

        public String getTruckId() {
            return truckId;
        }

        public void setTruckId(String truckId) {
            this.truckId = truckId;
        }

        public List<String> getOrderSequence() {
            return orderSequence;
        }

        public void setOrderSequence(List<String> orderSequence) {
            this.orderSequence = orderSequence;
        }

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public double getCost() {
            return cost;
        }

        public void setCost(double cost) {
            this.cost = cost;
        }
    }
}
