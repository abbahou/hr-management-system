package com.hrplatform.iam.controller;

public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        String userId,
        String email,
        String role) {
}
