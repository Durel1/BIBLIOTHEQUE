package com.durel.bibliotheque.exception;

/**
 * Raised when a book cannot be accessed by the authenticated user.
 *
 * We deliberately use the same exception whether the book does not exist
 * or belongs to another user. This avoids revealing another user's data.
 */
public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(Long bookId) {
        super("Book not found: " + bookId);
    }
}
