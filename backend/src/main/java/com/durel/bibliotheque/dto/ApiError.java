package com.durel.bibliotheque.dto;

import java.time.Instant;

/**
 * Standard error response returned by the REST API.
 *
 * Using one common structure makes errors predictable
 * for the frontend.
 */
public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path
) {
}
