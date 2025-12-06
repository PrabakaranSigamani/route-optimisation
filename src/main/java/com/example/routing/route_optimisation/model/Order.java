package com.example.routing.route_optimisation.model;

public class Order {
    private String id;
    private Location location;
    private int weight;
    private int length;
    private int width;
    private int height;
    private long serviceTime; // in minutes
    private long timeWindowStart; // in minutes from start of day
    private long timeWindowEnd; // in minutes from start of day
    private int priority; // 1 = high, 10 = low

    public Order() {
    }

    public Order(String id, Location location, int weight, int length, int width, int height, long serviceTime,
            long timeWindowStart, long timeWindowEnd,
            int priority) {
        this.id = id;
        this.location = location;
        this.weight = weight;
        this.length = length;
        this.width = width;
        this.height = height;
        this.serviceTime = serviceTime;
        this.timeWindowStart = timeWindowStart;
        this.timeWindowEnd = timeWindowEnd;
        this.priority = priority;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public long getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(long serviceTime) {
        this.serviceTime = serviceTime;
    }

    public long getTimeWindowStart() {
        return timeWindowStart;
    }

    public void setTimeWindowStart(long timeWindowStart) {
        this.timeWindowStart = timeWindowStart;
    }

    public long getTimeWindowEnd() {
        return timeWindowEnd;
    }

    public void setTimeWindowEnd(long timeWindowEnd) {
        this.timeWindowEnd = timeWindowEnd;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
