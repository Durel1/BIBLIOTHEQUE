package com.durel.bibliotheque.dto;

/**
 * Data returned after successful authentication.
 */
public record LoginResponse(
        Long id,
        String username,
        String email,
        String token
) {
}