package com.durel.bibliotheque.repository;

import com.durel.bibliotheque.entity.Book;
import com.durel.bibliotheque.entity.User;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Test
        void shouldSaveAndFindBookForUser() {

        User user = new User(
                "durel",
                "durel@example.com",
                "encoded-password"
        );

        User savedUser =
                userRepository.save(user);

        Book book = new Book(
                "Clean Code",
                "Robert C. Martin",
                2008,
                "Programming",
                "A book about clean code.",
                null
        );

        /*
        * A Book can no longer exist without an owner.
        */
        book.setUser(savedUser);

        Book savedBook =
                bookRepository.save(book);

        Optional<Book> result =
                bookRepository.findByIdAndUser_Id(
                        savedBook.getId(),
                        savedUser.getId()
                );

        assertTrue(result.isPresent());

        assertEquals(
                "Clean Code",
                result.get().getTitle()
        );

        assertEquals(
                savedUser.getId(),
                result.get().getUser().getId()
        );
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

    @Test
        void shouldFindOnlyBooksBelongingToUser() {

        User firstUser = new User(
                "durel",
                "durel@example.com",
                "encoded-password"
        );

        User secondUser = new User(
                "alice",
                "alice@example.com",
                "encoded-password"
        );

        userRepository.save(firstUser);
        userRepository.save(secondUser);

        Book firstBook = new Book(
                "Clean Code",
                "Robert C. Martin",
                2008,
                "Programming",
                "A book about clean code.",
                null
        );

        firstBook.setUser(firstUser);

        Book secondBook = new Book(
                "Effective Java",
                "Joshua Bloch",
                2018,
                "Programming",
                "A book about Java.",
                null
        );

        secondBook.setUser(secondUser);

        bookRepository.save(firstBook);
        bookRepository.save(secondBook);

        List<Book> books =
                bookRepository.findAllByUser_Id(
                        firstUser.getId()
                );

        assertEquals(1, books.size());
        assertEquals(
                "Clean Code",
                books.getFirst().getTitle()
        );

        assertEquals(
                firstUser.getId(),
                books.getFirst().getUser().getId()
        );
        }

        @Test
        void shouldNotFindBookBelongingToAnotherUser() {

        User firstUser = new User(
                "durel",
                "durel@example.com",
                "encoded-password"
        );

        User secondUser = new User(
                "alice",
                "alice@example.com",
                "encoded-password"
        );

        userRepository.save(firstUser);
        userRepository.save(secondUser);

        Book book = new Book(
                "Clean Code",
                "Robert C. Martin",
                2008,
                "Programming",
                "A book about clean code.",
                null
        );

        book.setUser(firstUser);

        Book savedBook =
                bookRepository.save(book);

        Optional<Book> result =
                bookRepository.findByIdAndUser_Id(
                        savedBook.getId(),
                        secondUser.getId()
                );

        assertTrue(result.isEmpty());
        }

}