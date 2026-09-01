package com.durel.bibliotheque.service;

import com.durel.bibliotheque.dto.BookResponse;
import com.durel.bibliotheque.dto.CreateBookRequest;
import com.durel.bibliotheque.dto.UpdateBookRequest;
import com.durel.bibliotheque.entity.Book;
import com.durel.bibliotheque.repository.BookRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Contains the business logic related to books.
 *
 * Book data is persisted through BookRepository instead of
 * being stored directly in memory by the service.
 */
@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;

    /**
     * Spring injects the repository used by this service.
     */
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * Returns all books stored in the database.
     */
    @Transactional(readOnly = true)
    public List<BookResponse> findAll() {

        return bookRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Finds one book by its identifier.
     */
    @Transactional(readOnly = true)
    public Optional<BookResponse> findById(Long id) {

        return bookRepository.findById(id)
                .map(this::toResponse);
    }

    /**
     * Creates and persists a new book.
     */
    public BookResponse create(CreateBookRequest request) {

        Book book = new Book(
                request.title().trim(),
                request.author().trim(),
                request.publishedYear(),
                normalizeOptionalText(request.genre()),
                normalizeOptionalText(request.description()),
                normalizeOptionalText(request.coverUrl())
        );

        Book savedBook = bookRepository.save(book);

        return toResponse(savedBook);
    }

    /**
     * Updates an existing book when it exists.
     */
    public Optional<BookResponse> update(
            Long id,
            UpdateBookRequest request) {

        Optional<Book> existingBook =
                bookRepository.findById(id);

        if (existingBook.isEmpty()) {
            return Optional.empty();
        }

        Book book = existingBook.get();

        book.setTitle(request.title().trim());
        book.setAuthor(request.author().trim());
        book.setPublishedYear(request.publishedYear());
        book.setGenre(normalizeOptionalText(request.genre()));
        book.setDescription(
                normalizeOptionalText(request.description())
        );
        book.setCoverUrl(
                normalizeOptionalText(request.coverUrl())
        );

        /*
         * saveAndFlush forces Hibernate to synchronize the update
         * before building the response. This is useful because
         * updatedAt is changed by the @PreUpdate callback.
         */
        Book updatedBook =
                bookRepository.saveAndFlush(book);

        return Optional.of(toResponse(updatedBook));
    }

    /**
     * Deletes a book when it exists.
     *
     * @return true when a book was deleted, false otherwise
     */
    public boolean delete(Long id) {

        Optional<Book> existingBook =
                bookRepository.findById(id);

        if (existingBook.isEmpty()) {
            return false;
        }

        bookRepository.delete(existingBook.get());

        return true;
    }

    /**
     * Converts a persisted Book entity into the DTO exposed by the API.
     */
    private BookResponse toResponse(Book book) {

        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getPublishedYear(),
                book.getGenre(),
                book.getDescription(),
                book.getCoverUrl(),
                book.getCreatedAt(),
                book.getUpdatedAt()
        );
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