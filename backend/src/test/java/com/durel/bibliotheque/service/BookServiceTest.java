package com.durel.bibliotheque.service;

import com.durel.bibliotheque.dto.BookResponse;
import com.durel.bibliotheque.dto.CreateBookRequest;
import com.durel.bibliotheque.dto.UpdateBookRequest;
import com.durel.bibliotheque.entity.Book;
import com.durel.bibliotheque.repository.BookRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void shouldCreateBook() {

        given(bookRepository.save(any(Book.class)))
                .willAnswer(invocation ->
                        invocation.getArgument(0)
                );

        CreateBookRequest request =
                new CreateBookRequest(
                        "  Clean Code  ",
                        "  Robert C. Martin  ",
                        2008,
                        " Software Engineering ",
                        " A book about clean code. ",
                        null
                );

        BookResponse result =
                bookService.create(request);

        assertEquals("Clean Code", result.title());
        assertEquals(
                "Robert C. Martin",
                result.author()
        );
        assertEquals(
                "Software Engineering",
                result.genre()
        );

        verify(bookRepository)
                .save(any(Book.class));
    }

    @Test
    void shouldReturnAllBooks() {

        Book firstBook = new Book(
                "Clean Code",
                "Robert C. Martin",
                2008,
                null,
                null,
                null
        );

        Book secondBook = new Book(
                "Effective Java",
                "Joshua Bloch",
                2018,
                null,
                null,
                null
        );

        given(bookRepository.findAll())
                .willReturn(
                        List.of(firstBook, secondBook)
                );

        List<BookResponse> result =
                bookService.findAll();

        assertEquals(2, result.size());
        assertEquals(
                "Clean Code",
                result.get(0).title()
        );
        assertEquals(
                "Effective Java",
                result.get(1).title()
        );
    }

    @Test
    void shouldFindBookById() {

        Book book = new Book(
                "Clean Code",
                "Robert C. Martin",
                2008,
                null,
                null,
                null
        );

        given(bookRepository.findById(1L))
                .willReturn(Optional.of(book));

        Optional<BookResponse> result =
                bookService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(
                "Clean Code",
                result.get().title()
        );
    }

    @Test
    void shouldReturnEmptyWhenBookDoesNotExist() {

        given(bookRepository.findById(999L))
                .willReturn(Optional.empty());

        Optional<BookResponse> result =
                bookService.findById(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldUpdateExistingBook() {

        Book book = new Book(
                "Clean Code",
                "Robert C. Martin",
                2008,
                null,
                null,
                null
        );

        given(bookRepository.findById(1L))
                .willReturn(Optional.of(book));

        given(bookRepository.saveAndFlush(book))
                .willReturn(book);

        UpdateBookRequest request =
                new UpdateBookRequest(
                        "Clean Code Updated",
                        "Robert C. Martin",
                        2008,
                        "Software Engineering",
                        "Updated description",
                        null
                );

        Optional<BookResponse> result =
                bookService.update(1L, request);

        assertTrue(result.isPresent());

        assertEquals(
                "Clean Code Updated",
                result.get().title()
        );

        assertEquals(
                "Updated description",
                result.get().description()
        );

        verify(bookRepository)
                .saveAndFlush(book);
    }

    @Test
    void shouldReturnEmptyWhenUpdatingMissingBook() {

        given(bookRepository.findById(999L))
                .willReturn(Optional.empty());

        UpdateBookRequest request =
                new UpdateBookRequest(
                        "Unknown Book",
                        "Unknown Author",
                        null,
                        null,
                        null,
                        null
                );

        Optional<BookResponse> result =
                bookService.update(999L, request);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldDeleteExistingBook() {

        Book book = new Book(
                "Clean Code",
                "Robert C. Martin",
                2008,
                null,
                null,
                null
        );

        given(bookRepository.findById(1L))
                .willReturn(Optional.of(book));

        boolean result =
                bookService.delete(1L);

        assertTrue(result);

        verify(bookRepository)
                .delete(book);
    }

    @Test
    void shouldReturnFalseWhenDeletingMissingBook() {

        given(bookRepository.findById(999L))
                .willReturn(Optional.empty());

        boolean result =
                bookService.delete(999L);

        assertFalse(result);
    }
}