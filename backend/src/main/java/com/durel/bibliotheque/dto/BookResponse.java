package com.durel.bibliotheque.dto;

import java.time.Instant;

/**
 * Data returned by the API when exposing a book to a client.
 */
public record BookResponse(

        Long id,
        String title,
        String author,
        Integer publishedYear,
        String genre,
        String description,
        String coverUrl,
        Instant createdAt,
        Instant updatedAt

) {
}
