package com.durel.bibliotheque.controller;

import com.durel.bibliotheque.dto.BookResponse;
import com.durel.bibliotheque.dto.CreateBookRequest;
import com.durel.bibliotheque.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.durel.bibliotheque.dto.UpdateBookRequest;

import java.net.URI;
import java.util.List;

/**
 * Exposes REST endpoints used to manage books.
 *
 * The controller is responsible for HTTP concerns only.
 * Business logic is delegated to BookService.
 */
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    /**
     * Spring injects the BookService dependency through the constructor.
     */
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * Returns all books currently stored.
     */
    @GetMapping
    public List<BookResponse> findAll() {
        return bookService.findAll();
    }

    /**
     * Returns one book when it exists, otherwise HTTP 404.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> findById(@PathVariable Long id) {

        return bookService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Creates a new book and returns HTTP 201 Created.
     */
    @PostMapping
    public ResponseEntity<BookResponse> create(
            @Valid @RequestBody CreateBookRequest request) {

        BookResponse createdBook = bookService.create(request);

        URI location = URI.create(
                "/api/books/" + createdBook.id()
        );

        return ResponseEntity
                .created(location)
                .body(createdBook);
    }

    /**
     * Updates an existing book.
     */
    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBookRequest request) {

        return bookService.update(id, request)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Deletes an existing book.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        if (!bookService.delete(id)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}