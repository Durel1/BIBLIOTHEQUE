package com.durel.bibliotheque.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.durel.bibliotheque.dto.BookResponse;
import com.durel.bibliotheque.dto.CreateBookRequest;
import com.durel.bibliotheque.dto.UpdateBookRequest;
import com.durel.bibliotheque.entity.Book;
import com.durel.bibliotheque.entity.User;
import com.durel.bibliotheque.repository.BookRepository;

/**
 * Contains the business logic related to books.
 *
 * Every read, update and delete operation is scoped to a user
 * so that one authenticated user cannot access another user's books.
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
     * Returns only books belonging to the authenticated user.
     */
    @Transactional(readOnly = true)
    public List<BookResponse> findAllByUserId(Long userId) {

        return bookRepository
                .findAllByUser_Id(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Returns a book only when it belongs to the authenticated user.
     *
     * If the book does not exist or belongs to another user,
     * an empty Optional is returned.
     */
    @Transactional(readOnly = true)
    public Optional<BookResponse> findByIdForUser(
            Long bookId,
            Long userId) {

        return bookRepository
                .findByIdAndUser_Id(
                        bookId,
                        userId
                )
                .map(this::toResponse);
    }

    /**
     * Creates a book owned by the authenticated user.
     *
     * The owner comes from the server-side authentication context,
     * never from client input.
     */
    public BookResponse create(
            CreateBookRequest request,
            User authenticatedUser) {

        Book book = new Book(
                request.title(),
                request.author(),
                request.publishedYear(),
                request.genre(),
                request.description(),
                request.coverUrl()
        );

        /*
         * Ownership is assigned by the backend.
         * The client cannot choose another user as the owner.
         */
        book.setUser(authenticatedUser);

        Book savedBook =
                bookRepository.save(book);

        return toResponse(savedBook);
    }

    /**
     * Updates a book only when it belongs to the authenticated user.
     *
     * If the book does not exist or belongs to another user,
     * an empty Optional is returned.
     */
    public Optional<BookResponse> updateForUser(
            Long bookId,
            Long userId,
            UpdateBookRequest request) {

        return bookRepository
                .findByIdAndUser_Id(
                        bookId,
                        userId
                )
                .map(book -> {

                    /*
                     * Only book data is updated.
                     * Ownership must never be changed here.
                     */
                    book.setTitle(
                            request.title().trim()
                    );

                    book.setAuthor(
                            request.author().trim()
                    );

                    book.setPublishedYear(
                            request.publishedYear()
                    );

                    book.setGenre(
                            normalizeOptionalText(
                                    request.genre()
                            )
                    );

                    book.setDescription(
                            normalizeOptionalText(
                                    request.description()
                            )
                    );

                    book.setCoverUrl(
                            normalizeOptionalText(
                                    request.coverUrl()
                            )
                    );

                    /*
                     * saveAndFlush synchronizes the update immediately.
                     * This ensures that @PreUpdate updates updatedAt
                     * before the response DTO is created.
                     */
                    Book updatedBook =
                            bookRepository.saveAndFlush(book);

                    return toResponse(updatedBook);
                });
    }

    /**
     * Deletes a book only when it belongs to the authenticated user.
     *
     * Returns false when the book does not exist
     * or belongs to another user.
     */
    public boolean deleteForUser(
            Long bookId,
            Long userId) {

        return bookRepository
                .findByIdAndUser_Id(
                        bookId,
                        userId
                )
                .map(book -> {

                    bookRepository.delete(book);

                    return true;
                })
                .orElse(false);
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
     * Converts blank optional text to null and trims meaningful values.
     */
    private String normalizeOptionalText(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}