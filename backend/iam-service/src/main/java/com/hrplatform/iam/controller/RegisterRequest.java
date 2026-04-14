package com.hrplatform.iam.controller;

import com.hrplatform.iam.model.Role;

public record RegisterRequest(String email, String password, Role role) {
}

