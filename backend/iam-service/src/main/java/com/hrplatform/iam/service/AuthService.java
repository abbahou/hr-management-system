package com.hrplatform.iam.service;

import com.hrplatform.iam.controller.AuthResponse;
import com.hrplatform.iam.controller.CurrentUserResponse;
import com.hrplatform.iam.controller.LoginRequest;
import com.hrplatform.iam.controller.RegisterRequest;
import com.hrplatform.iam.model.Role;
import com.hrplatform.iam.model.User;
import com.hrplatform.iam.repository.UserRepository;
import java.time.Instant;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private static final long TOKEN_TTL_SECONDS = 3600L;
    private static final int MIN_PASSWORD_LENGTH = 8;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtEncoder jwtEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
    }

    public AuthResponse register(RegisterRequest request) {
        String normalizedEmail = normalizeEmail(request.email());
        validateCredentials(normalizedEmail, request.password());

        if (userRepository.findByEmail(normalizedEmail).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }

        User user = new User();
        user.setEmail(normalizedEmail);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(request.role() == null ? Role.EMPLOYEE : request.role());
        user.setLastLoginDate(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        return issueToken(savedUser);
    }

    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = normalizeEmail(request.email());
        validateCredentials(normalizedEmail, request.password());
        User user = userRepository.findByEmail(normalizedEmail)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        user.setLastLoginDate(LocalDateTime.now());
        User updatedUser = userRepository.save(user);

        return issueToken(updatedUser);
    }

    public CurrentUserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(normalizeEmail(email))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        return new CurrentUserResponse(
            user.getUserId(),
            user.getEmail(),
            user.getRole().name(),
            user.getLastLoginDate()
        );
    }

    private void validateCredentials(String email, String password) {
        if (!StringUtils.hasText(email) || !StringUtils.hasText(password)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email and password are required");
        }

        if (!email.contains("@")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email format is invalid");
        }

        if (password.trim().length() < MIN_PASSWORD_LENGTH) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Password must be at least " + MIN_PASSWORD_LENGTH + " characters"
            );
        }
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    private AuthResponse issueToken(User user) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusSeconds(TOKEN_TTL_SECONDS);

        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer("hrplatform-iam")
            .subject(user.getEmail())
            .issuedAt(issuedAt)
            .expiresAt(expiresAt)
            .claim("email", user.getEmail())
            .claim("role", user.getRole().name())
            .claim("uid", user.getUserId().toString())
            .build();

        String token = jwtEncoder
            .encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims))
            .getTokenValue();

        return new AuthResponse(
            token,
            "Bearer",
            TOKEN_TTL_SECONDS,
            user.getUserId(),
            user.getEmail(),
            user.getRole().name()
        );
    }
}

