package com.durel.bibliotheque.service;

import com.durel.bibliotheque.dto.BookResponse;
import com.durel.bibliotheque.dto.CreateBookRequest;
import com.durel.bibliotheque.dto.UpdateBookRequest;
import com.durel.bibliotheque.entity.Book;
import com.durel.bibliotheque.entity.User;
import com.durel.bibliotheque.repository.BookRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void shouldReturnOnlyBooksBelongingToUser() {

        Book book = new Book(
                "Clean Code",
                "Robert C. Martin",
                2008,
                "Programming",
                "A book about clean code.",
                null
        );

        given(
                bookRepository.findAllByUser_Id(1L)
        ).willReturn(List.of(book));

        List<BookResponse> result =
                bookService.findAllByUserId(1L);

        assertEquals(
                1,
                result.size()
        );

        assertEquals(
                "Clean Code",
                result.getFirst().title()
        );

        verify(bookRepository)
                .findAllByUser_Id(1L);
    }

    @Test
    void shouldCreateBookForAuthenticatedUser() {

        CreateBookRequest request =
                new CreateBookRequest(
                        "Clean Code",
                        "Robert C. Martin",
                        2008,
                        "Software Engineering",
                        "A book about clean code.",
                        null
                );

        User user = new User(
                "durel",
                "durel@example.com",
                "encoded-password"
        );

        /*
         * The repository returns the same Book it receives.
         * This simulates persistence without a real database.
         */
        given(
                bookRepository.save(
                        any(Book.class)
                )
        ).willAnswer(invocation ->
                invocation.getArgument(0)
        );

        BookResponse response =
                bookService.create(
                        request,
                        user
                );

        assertEquals(
                "Clean Code",
                response.title()
        );

        assertEquals(
                "Robert C. Martin",
                response.author()
        );

        ArgumentCaptor<Book> bookCaptor =
                ArgumentCaptor.forClass(Book.class);

        verify(bookRepository)
                .save(bookCaptor.capture());

        Book savedBook =
                bookCaptor.getValue();

        /*
         * Proves that ownership is assigned by the backend.
         */
        assertEquals(
                user,
                savedBook.getUser()
        );
    }

    @Test
    void shouldReturnBookWhenItBelongsToUser() {

        Book book = new Book(
                "Clean Code",
                "Robert C. Martin",
                2008,
                "Programming",
                "A book about clean code.",
                null
        );

        given(
                bookRepository.findByIdAndUser_Id(
                        10L,
                        1L
                )
        ).willReturn(Optional.of(book));

        Optional<BookResponse> result =
                bookService.findByIdForUser(
                        10L,
                        1L
                );

        assertTrue(
                result.isPresent()
        );

        assertEquals(
                "Clean Code",
                result.get().title()
        );

        verify(bookRepository)
                .findByIdAndUser_Id(
                        10L,
                        1L
                );
    }

    @Test
    void shouldReturnEmptyWhenBookDoesNotBelongToUser() {

        given(
                bookRepository.findByIdAndUser_Id(
                        10L,
                        2L
                )
        ).willReturn(Optional.empty());

        Optional<BookResponse> result =
                bookService.findByIdForUser(
                        10L,
                        2L
                );

        assertTrue(
                result.isEmpty()
        );

        verify(bookRepository)
                .findByIdAndUser_Id(
                        10L,
                        2L
                );
    }

    @Test
    void shouldUpdateBookWhenItBelongsToUser() {

        Book book = new Book(
                "Clean Code",
                "Robert C. Martin",
                2008,
                "Programming",
                "Old description",
                null
        );

        UpdateBookRequest request =
                new UpdateBookRequest(
                        "Clean Code Updated",
                        "Robert C. Martin",
                        2008,
                        "Software Engineering",
                        "Updated description",
                        null
                );

        given(
                bookRepository.findByIdAndUser_Id(
                        10L,
                        1L
                )
        ).willReturn(Optional.of(book));

        given(
                bookRepository.saveAndFlush(book)
        ).willReturn(book);

        Optional<BookResponse> result =
                bookService.updateForUser(
                        10L,
                        1L,
                        request
                );

        assertTrue(
                result.isPresent()
        );

        assertEquals(
                "Clean Code Updated",
                result.get().title()
        );

        assertEquals(
                "Updated description",
                result.get().description()
        );

        verify(bookRepository)
                .findByIdAndUser_Id(
                        10L,
                        1L
                );

        verify(bookRepository)
                .saveAndFlush(book);
    }

    @Test
    void shouldNotUpdateBookBelongingToAnotherUser() {

        UpdateBookRequest request =
                new UpdateBookRequest(
                        "Hacked title",
                        "Unknown",
                        2026,
                        "Unknown",
                        "Should never be applied",
                        null
                );

        given(
                bookRepository.findByIdAndUser_Id(
                        10L,
                        2L
                )
        ).willReturn(Optional.empty());

        Optional<BookResponse> result =
                bookService.updateForUser(
                        10L,
                        2L,
                        request
                );

        assertTrue(
                result.isEmpty()
        );

        verify(bookRepository)
                .findByIdAndUser_Id(
                        10L,
                        2L
                );

        /*
         * A book belonging to another user must never
         * reach the persistence update operation.
         */
        verify(bookRepository, never())
                .saveAndFlush(
                        any(Book.class)
                );
    }

    @Test
    void shouldDeleteBookWhenItBelongsToUser() {

        Book book = new Book(
                "Clean Code",
                "Robert C. Martin",
                2008,
                "Programming",
                "A book about clean code.",
                null
        );

        given(
                bookRepository.findByIdAndUser_Id(
                        10L,
                        1L
                )
        ).willReturn(Optional.of(book));

        boolean deleted =
                bookService.deleteForUser(
                        10L,
                        1L
                );

        assertTrue(deleted);

        verify(bookRepository)
                .findByIdAndUser_Id(
                        10L,
                        1L
                );

        verify(bookRepository)
                .delete(book);
    }

    @Test
    void shouldNotDeleteBookBelongingToAnotherUser() {

        given(
                bookRepository.findByIdAndUser_Id(
                        10L,
                        2L
                )
        ).willReturn(Optional.empty());

        boolean deleted =
                bookService.deleteForUser(
                        10L,
                        2L
                );

        assertFalse(deleted);

        verify(bookRepository)
                .findByIdAndUser_Id(
                        10L,
                        2L
                );

        /*
         * No delete operation must occur when ownership
         * does not match the authenticated user.
         */
        verify(bookRepository, never())
                .delete(
                        any(Book.class)
                );
    }
}