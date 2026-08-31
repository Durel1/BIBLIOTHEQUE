package com.durel.bibliotheque.service;

import com.durel.bibliotheque.dto.BookResponse;
import com.durel.bibliotheque.dto.CreateBookRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Contains the business logic related to books.
 *
 * Books are currently stored in memory for learning purposes.
 * This temporary storage will later be replaced by PostgreSQL
 * through Spring Data JPA.
 */
@Service
public class BookService {

    private final Map<Long, BookResponse> books = new LinkedHashMap<>();

    private long nextId = 1L;

    /**
     * Returns all books currently stored in memory.
     */
    public List<BookResponse> findAll() {
        return new ArrayList<>(books.values());
    }

    /**
     * Finds a book by its identifier.
     *
     * Optional is used because a book with the requested ID
     * may not exist.
     */
    public Optional<BookResponse> findById(Long id) {
        return Optional.ofNullable(books.get(id));
    }

    /**
     * Creates a new book from the data received by the API.
     */
    public BookResponse create(CreateBookRequest request) {

        Instant now = Instant.now();

        BookResponse book = new BookResponse(
                nextId++,
                request.title().trim(),
                request.author().trim(),
                request.publishedYear(),
                normalizeOptionalText(request.genre()),
                normalizeOptionalText(request.description()),
                normalizeOptionalText(request.coverUrl()),
                now,
                now
        );

        books.put(book.id(), book);

        return book;
    }

    /**
     * Converts an optional blank string into null and trims
     * meaningful values.
     */
    private String normalizeOptionalText(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}