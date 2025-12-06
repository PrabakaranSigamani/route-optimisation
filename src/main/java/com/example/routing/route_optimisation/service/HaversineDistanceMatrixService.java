package com.example.routing.route_optimisation.service;

import com.example.routing.route_optimisation.model.Location;
import org.springframework.stereotype.Service;

@Service
public class HaversineDistanceMatrixService implements DistanceMatrixService {

    private static final int EARTH_RADIUS = 6371; // Radius of the earth in km
    private static final int AVERAGE_SPEED_KMH = 40; // Average speed in km/h

    @Override
    public void initMatrix(java.util.List<Location> locations) {
        // No-op for Haversine, calculation is on-the-fly
    }

    @Override
    public double getDistance(Location from, Location to) {
        return calculateHaversineDistance(from.getLatitude(), from.getLongitude(),
                to.getLatitude(), to.getLongitude());
    }

    @Override
    public long getTime(Location from, Location to) {
        double distance = getDistance(from, to);
        return (long) ((distance / AVERAGE_SPEED_KMH) * 60); // Time in minutes
    }

    private double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }
}
