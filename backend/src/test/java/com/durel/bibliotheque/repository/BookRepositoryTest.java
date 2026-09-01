package com.durel.bibliotheque.repository;

import com.durel.bibliotheque.entity.Book;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Test
    void shouldSaveAndFindBook() {

        Book book = new Book(
                "Clean Code",
                "Robert C. Martin",
                2008,
                "Software Engineering",
                "A book about writing cleaner code.",
                null
        );

        Book savedBook = bookRepository.save(book);

        assertNotNull(savedBook.getId());

        Optional<Book> result =
                bookRepository.findById(savedBook.getId());

        assertTrue(result.isPresent());
        assertEquals("Clean Code", result.get().getTitle());
        assertEquals("Robert C. Martin", result.get().getAuthor());
    }
}