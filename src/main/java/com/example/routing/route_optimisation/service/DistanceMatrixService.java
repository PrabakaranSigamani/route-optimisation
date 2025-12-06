package com.example.routing.route_optimisation.service;

import com.example.routing.route_optimisation.model.Location;

public interface DistanceMatrixService {
    void initMatrix(java.util.List<Location> locations);

    double getDistance(Location from, Location to); // in kms

    long getTime(Location from, Location to); // in minutes
}
