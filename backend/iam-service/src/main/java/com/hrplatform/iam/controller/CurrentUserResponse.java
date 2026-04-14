package com.hrplatform.iam.controller;

import java.time.LocalDateTime;
import java.util.UUID;

public record CurrentUserResponse(
    UUID userId,
    String email,
    String role,
    LocalDateTime lastLoginDate
) {
}

