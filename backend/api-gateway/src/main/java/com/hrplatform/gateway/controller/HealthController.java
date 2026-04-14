package com.hrplatform.gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "API Gateway");
        response.put("timestamp", LocalDateTime.now());
        response.put("message", "API Gateway is running");
        return response;
    }

    @GetMapping("/")
    public Map<String, Object> info() {
        Map<String, Object> response = new HashMap<>();
        response.put("service", "HR Platform - API Gateway");
        response.put("version", "1.0.0");
        response.put("routes", new String[]{
            "/auth/** -> IAM Service",
            "/recruitment/** -> Recruitment Service",
            "/employees/** -> Employee Service"
        });
        return response;
    }
}

