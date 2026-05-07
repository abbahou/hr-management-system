package com.hrplatform.iam.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true", allowedHeaders = "*", methods = {
        RequestMethod.GET, RequestMethod.OPTIONS })
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("IAM Service is up and running! Connected to Keycloak and Postgres.");
    }

    @RequestMapping(path = "/health", method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> handleOptionsHealth() {
        return ResponseEntity.ok().build();
    }
}
