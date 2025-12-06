package com.example.routing.route_optimisation.service;

import com.example.routing.route_optimisation.model.Location;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Service
@Primary
public class OSMDistanceMatrixService implements DistanceMatrixService {

    private static final Logger logger = Logger.getLogger(OSMDistanceMatrixService.class.getName());

    @Value("${osrm.baseUrl:http://router.project-osrm.org}")
    private String osrmBaseUrl;

    private final Map<String, Double> distanceCache = new HashMap<>(); // Key -> km
    private final Map<String, Long> timeCache = new HashMap<>(); // Key -> minutes
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public OSMDistanceMatrixService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void initMatrix(List<Location> locations) {
        if (locations == null || locations.isEmpty())
            return;

        logger.info("Initializing Distance Matrix for " + locations.size() + " locations using OSRM Table API...");

        // Construct coordinate string: "lon,lat;lon,lat;..."
        StringBuilder coords = new StringBuilder();
        for (int i = 0; i < locations.size(); i++) {
            Location loc = locations.get(i);
            coords.append(loc.getLongitude()).append(",").append(loc.getLatitude());
            if (i < locations.size() - 1) {
                coords.append(";");
            }
        }

        // Call OSRM Table Service
        // http://{baseUrl}/table/v1/driving/{coords}?annotations=distance,duration
        String url = String.format("%s/table/v1/driving/%s?annotations=distance,duration", osrmBaseUrl,
                coords.toString());

        try {
            String jsonResponse = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(jsonResponse);

            if (!root.has("code") || !"Ok".equals(root.get("code").asText())) {
                logger.severe(
                        "OSRM API returned error: " + (root.has("message") ? root.get("message").asText() : "Unknown"));
                return;
            }

            JsonNode durations = root.get("durations"); // in seconds
            JsonNode distances = root.get("distances"); // in meters

            // Parse response into cache
            // OSRM returns N x N arrays corresponding to input order
            for (int i = 0; i < locations.size(); i++) {
                for (int j = 0; j < locations.size(); j++) {
                    if (i == j)
                        continue;

                    Location from = locations.get(i);
                    Location to = locations.get(j);
                    String key = generateKey(from, to);

                    if (distances != null) {
                        double distMeters = distances.get(i).get(j).asDouble();
                        distanceCache.put(key, distMeters / 1000.0); // Convert to km
                        // logger.info("Cached " + key + " :: " + (distMeters / 1000.0) + " km");
                    }

                    if (durations != null) {
                        double durationSeconds = durations.get(i).get(j).asDouble();
                        timeCache.put(key, (long) (durationSeconds / 60)); // Convert to minutes
                    }
                }
            }
            logger.info("Distance Matrix Initialization Complete. Cached " + distanceCache.size() + " routes.");

        } catch (Exception e) {
            logger.severe("Failed to fetch distance matrix from OSRM: " + e.getMessage());
        }
    }

    @Override
    public double getDistance(Location from, Location to) {
        String key = generateKey(from, to);
        double val = distanceCache.getOrDefault(key, 0.0);
        // logger.info("getDistance: " + key + " = " + val);
        return val;
    }

    @Override
    public long getTime(Location from, Location to) {
        String key = generateKey(from, to);
        return timeCache.getOrDefault(key, 0L);
    }

    private String generateKey(Location from, Location to) {
        return from.getLatitude() + "," + from.getLongitude() + "->" + to.getLatitude() + "," + to.getLongitude();
    }
}
