package com.durel.bibliotheque.controller;

import com.durel.bibliotheque.dto.BookResponse;
import com.durel.bibliotheque.dto.CreateBookRequest;
import com.durel.bibliotheque.dto.UpdateBookRequest;
import com.durel.bibliotheque.entity.User;
import com.durel.bibliotheque.exception.BookNotFoundException;
import com.durel.bibliotheque.service.BookService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

/**
 * Exposes REST endpoints used to manage books.
 *
 * The controller handles HTTP concerns only.
 * Business logic is delegated to BookService.
 */
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * Returns only books belonging to the authenticated user.
     *
     * The User comes from Spring Security's SecurityContext,
     * not from data sent by the client.
     */
    @GetMapping
    public List<BookResponse> findAll(
            @AuthenticationPrincipal User user) {

        return bookService.findAllByUserId(
                user.getId()
        );
    }

    /**
     * Returns a book only when it belongs to the authenticated user.
     *
     * A missing book and a book owned by another user both
     * result in the same HTTP 404 response.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> findById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        BookResponse book = bookService
                .findByIdForUser(
                        id,
                        user.getId()
                )
                .orElseThrow(() ->
                        new BookNotFoundException(id)
                );

        return ResponseEntity.ok(book);
    }

    /**
     * Creates a new book for the authenticated user.
     *
     * The owner is obtained from Spring Security,
     * never from the request body.
     */
    @PostMapping
    public ResponseEntity<BookResponse> create(
            @Valid @RequestBody CreateBookRequest request,
            @AuthenticationPrincipal User user) {

        BookResponse createdBook =
                bookService.create(
                        request,
                        user
                );

        URI location = URI.create(
                "/api/books/" + createdBook.id()
        );

        return ResponseEntity
                .created(location)
                .body(createdBook);
    }

    /**
     * Updates a book only when it belongs to the authenticated user.
     *
     * A missing book and a book owned by another user both
     * result in the same HTTP 404 response.
     */
    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBookRequest request,
            @AuthenticationPrincipal User user) {

        BookResponse updatedBook = bookService
                .updateForUser(
                        id,
                        user.getId(),
                        request
                )
                .orElseThrow(() ->
                        new BookNotFoundException(id)
                );

        return ResponseEntity.ok(updatedBook);
    }

    /**
     * Deletes a book only when it belongs to the authenticated user.
     *
     * A missing book and a book owned by another user both
     * result in the same HTTP 404 response.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        boolean deleted = bookService.deleteForUser(
                id,
                user.getId()
        );

        if (!deleted) {
            throw new BookNotFoundException(id);
        }

        return ResponseEntity.noContent().build();
    }
}