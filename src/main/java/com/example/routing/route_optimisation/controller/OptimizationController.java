package com.example.routing.route_optimisation.controller;

import com.example.routing.route_optimisation.dto.RouteOptimizationRequest;
import com.example.routing.route_optimisation.dto.RouteOptimizationResponse;
import com.example.routing.route_optimisation.service.RouteOptimizationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/optimize")
public class OptimizationController {

    private final RouteOptimizationService routeOptimizationService;

    public OptimizationController(RouteOptimizationService routeOptimizationService) {
        this.routeOptimizationService = routeOptimizationService;
    }

    @PostMapping
    public ResponseEntity<RouteOptimizationResponse> optimizeRoute(@RequestBody RouteOptimizationRequest request) {
        RouteOptimizationResponse response = routeOptimizationService.optimize(request);
        return ResponseEntity.ok(response);
    }
}
