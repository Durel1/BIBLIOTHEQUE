package com.durel.bibliotheque.repository;

import com.durel.bibliotheque.entity.Book;
import com.durel.bibliotheque.entity.User;

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

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldAssociateBookWithUser() {

        User user = new User(
                "durel",
                "durel@example.com",
                "hashed-password"
        );

        User savedUser = userRepository.save(user);

        Book book = new Book(
                "Clean Code",
                "Robert C. Martin",
                2008,
                "Software Engineering",
                null,
                null
        );

        book.setUser(savedUser);

        Book savedBook = bookRepository.save(book);

        Optional<Book> result =
                bookRepository.findById(savedBook.getId());

        assertTrue(result.isPresent());
        assertNotNull(result.get().getUser());

        assertEquals(
                savedUser.getId(),
                result.get().getUser().getId()
        );
    }

}