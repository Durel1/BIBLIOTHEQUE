package com.durel.bibliotheque.dto;

import java.time.Instant;

/**
 * Data returned after a successful user registration.
 */
public record RegisterResponse(
        Long id,
        String username,
        String email,
        Instant createdAt
) {
}
