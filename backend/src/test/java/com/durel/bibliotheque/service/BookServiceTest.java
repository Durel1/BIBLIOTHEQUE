package com.durel.bibliotheque.service;

import com.durel.bibliotheque.dto.BookResponse;
import com.durel.bibliotheque.dto.CreateBookRequest;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BookServiceTest {

    @Test
    void shouldCreateBook() {

        BookService service = new BookService();

        CreateBookRequest request = new CreateBookRequest(
                "Clean Code",
                "Robert C. Martin",
                2008,
                "Software Engineering",
                "A book about clean code.",
                null
        );

        BookResponse createdBook = service.create(request);

        assertNotNull(createdBook.id());
        assertEquals(1L, createdBook.id());
        assertEquals("Clean Code", createdBook.title());
        assertEquals("Robert C. Martin", createdBook.author());
        assertNotNull(createdBook.createdAt());
        assertNotNull(createdBook.updatedAt());
    }

    @Test
    void shouldReturnCreatedBooks() {

        BookService service = new BookService();

        service.create(new CreateBookRequest(
                "Clean Code",
                "Robert C. Martin",
                2008,
                null,
                null,
                null
        ));

        service.create(new CreateBookRequest(
                "Effective Java",
                "Joshua Bloch",
                2018,
                null,
                null,
                null
        ));

        assertEquals(2, service.findAll().size());
    }

    @Test
    void shouldFindBookById() {

        BookService service = new BookService();

        BookResponse createdBook = service.create(
                new CreateBookRequest(
                        "Clean Code",
                        "Robert C. Martin",
                        2008,
                        null,
                        null,
                        null
                )
        );

        Optional<BookResponse> result =
                service.findById(createdBook.id());

        assertTrue(result.isPresent());
        assertEquals("Clean Code", result.get().title());
    }

    @Test
    void shouldReturnEmptyWhenBookDoesNotExist() {

        BookService service = new BookService();

        Optional<BookResponse> result =
                service.findById(999L);

        assertTrue(result.isEmpty());
    }
}