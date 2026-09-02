package com.durel.bibliotheque.repository;

import com.durel.bibliotheque.entity.Book;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository
        extends JpaRepository<Book, Long> {

    /**
     * Returns only books belonging to the given user.
     */
    List<Book> findAllByUser_Id(Long userId);

    /**
     * Returns a book only when both its ID and owner match.
     *
     * This prevents one authenticated user from accessing
     * another user's book by guessing its ID.
     */
    Optional<Book> findByIdAndUser_Id(
            Long bookId,
            Long userId
    );
}