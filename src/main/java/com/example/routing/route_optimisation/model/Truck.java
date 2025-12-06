package com.example.routing.route_optimisation.model;

public class Truck {
    private String id;
    private int weightCapacity;
    private int lengthCapacity;
    private int widthCapacity;
    private int heightCapacity;

    // Cost factors
    private double costPerKm;
    private double costPerHour;
    private double fixedCost;

    // Constraints
    private int maxStops;
    private double speedKmH; // Average speed for this truck

    private Location startLocation;
    private long availableFrom; // in minutes from start of day
    private long availableTo; // in minutes from start of day

    public Truck() {
    }

    public Truck(String id, int weightCapacity, int lengthCapacity, int widthCapacity, int heightCapacity,
            double costPerKm, double costPerHour, double fixedCost, int maxStops, double speedKmH,
            Location startLocation, long availableFrom, long availableTo) {
        this.id = id;
        this.weightCapacity = weightCapacity;
        this.lengthCapacity = lengthCapacity;
        this.widthCapacity = widthCapacity;
        this.heightCapacity = heightCapacity;
        this.costPerKm = costPerKm;
        this.costPerHour = costPerHour;
        this.fixedCost = fixedCost;
        this.maxStops = maxStops;
        this.speedKmH = speedKmH;
        this.startLocation = startLocation;
        this.availableFrom = availableFrom;
        this.availableTo = availableTo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getWeightCapacity() {
        return weightCapacity;
    }

    public void setWeightCapacity(int weightCapacity) {
        this.weightCapacity = weightCapacity;
    }

    public int getLengthCapacity() {
        return lengthCapacity;
    }

    public void setLengthCapacity(int lengthCapacity) {
        this.lengthCapacity = lengthCapacity;
    }

    public int getWidthCapacity() {
        return widthCapacity;
    }

    public void setWidthCapacity(int widthCapacity) {
        this.widthCapacity = widthCapacity;
    }

    public int getHeightCapacity() {
        return heightCapacity;
    }

    public void setHeightCapacity(int heightCapacity) {
        this.heightCapacity = heightCapacity;
    }

    public double getCostPerKm() {
        return costPerKm;
    }

    public void setCostPerKm(double costPerKm) {
        this.costPerKm = costPerKm;
    }

    public double getCostPerHour() {
        return costPerHour;
    }

    public void setCostPerHour(double costPerHour) {
        this.costPerHour = costPerHour;
    }

    public double getFixedCost() {
        return fixedCost;
    }

    public void setFixedCost(double fixedCost) {
        this.fixedCost = fixedCost;
    }

    public int getMaxStops() {
        return maxStops;
    }

    public void setMaxStops(int maxStops) {
        this.maxStops = maxStops;
    }

    public double getSpeedKmH() {
        return speedKmH;
    }

    public void setSpeedKmH(double speedKmH) {
        this.speedKmH = speedKmH;
    }

    public Location getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(Location startLocation) {
        this.startLocation = startLocation;
    }

    public long getAvailableFrom() {
        return availableFrom;
    }

    public void setAvailableFrom(long availableFrom) {
        this.availableFrom = availableFrom;
    }

    public long getAvailableTo() {
        return availableTo;
    }

    public void setAvailableTo(long availableTo) {
        this.availableTo = availableTo;
    }
}
