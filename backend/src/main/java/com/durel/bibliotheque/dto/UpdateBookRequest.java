package com.durel.bibliotheque.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data received from the client when updating an existing book.
 */
public record UpdateBookRequest(

        @NotBlank(message = "Title is required")
        String title,

        @NotBlank(message = "Author is required")
        String author,

        @Min(value = 1, message = "Published year must be greater than 0")
        Integer publishedYear,

        @Size(max = 100, message = "Genre must not exceed 100 characters")
        String genre,

        @Size(max = 2000, message = "Description must not exceed 2000 characters")
        String description,

        String coverUrl

) {
}
