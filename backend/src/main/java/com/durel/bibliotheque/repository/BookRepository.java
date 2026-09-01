package com.durel.bibliotheque.repository;

import com.durel.bibliotheque.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Provides database access operations for Book entities.
 */
public interface BookRepository extends JpaRepository<Book, Long> {
}