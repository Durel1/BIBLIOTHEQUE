package com.durel.bibliotheque.dto;

/**
 * Data returned after successful authentication.
 *
 * A JWT token will be added in a later step.
 */
public record LoginResponse(
        Long id,
        String username,
        String email
) {
}
