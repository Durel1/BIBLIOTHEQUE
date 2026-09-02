package com.durel.bibliotheque.controller;

import com.durel.bibliotheque.dto.BookResponse;
import com.durel.bibliotheque.dto.CreateBookRequest;
import com.durel.bibliotheque.dto.UpdateBookRequest;
import com.durel.bibliotheque.entity.User;
import com.durel.bibliotheque.security.JwtAuthenticationFilter;
import com.durel.bibliotheque.service.BookService;
import com.durel.bibliotheque.exception.BookNotFoundException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertThrows;

@WebMvcTest(BookController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private User authenticatedUser;

    /**
     * Creates an authenticated User in Spring Security's context
     * before each controller test.
     *
     * The User is mocked because this test does not use JPA or
     * the database, which would normally generate its ID.
     */
    @BeforeEach
    void setUpAuthentication() {

        authenticatedUser = mock(User.class);

        given(authenticatedUser.getId())
                .willReturn(1L);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        authenticatedUser,
                        null,
                        Collections.emptyList()
                );

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);
    }

    /**
     * Clears the thread-local SecurityContext after each test
     * to prevent authentication from leaking between tests.
     */
    @AfterEach
    void clearAuthentication() {

        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldReturnAllBooksForAuthenticatedUser() {

        BookResponse book =
                createSampleBook();

        given(
                bookService.findAllByUserId(1L)
        ).willReturn(List.of(book));

        BookController controller =
                new BookController(bookService);

        List<BookResponse> books =
                controller.findAll(authenticatedUser);

        assertEquals(
                1,
                books.size()
        );

        assertEquals(
                "Clean Code",
                books.getFirst().title()
        );

        assertEquals(
                "Robert C. Martin",
                books.getFirst().author()
        );

        verify(bookService)
                .findAllByUserId(1L);
    }

    @Test
    void shouldReturnBookByIdForAuthenticatedUser() {

        BookResponse book =
                createSampleBook();

        given(
                bookService.findByIdForUser(
                        1L,
                        1L
                )
        ).willReturn(Optional.of(book));

        BookController controller =
                new BookController(bookService);

        ResponseEntity<BookResponse> response =
                controller.findById(
                        1L,
                        authenticatedUser
                );

        assertEquals(
                200,
                response.getStatusCode().value()
        );

        assertEquals(
                "Clean Code",
                response.getBody().title()
        );

        verify(bookService)
                .findByIdForUser(
                        1L,
                        1L
                );
    }

    @Test
    void shouldReturnNotFoundWhenBookIsUnavailableForUser() {

        given(
                bookService.findByIdForUser(
                        999L,
                        1L
                )
        ).willReturn(Optional.empty());

        BookController controller =
                new BookController(bookService);

        BookNotFoundException exception =
                assertThrows(
                        BookNotFoundException.class,
                        () -> controller.findById(
                                999L,
                                authenticatedUser
                        )
                );

        assertEquals(
                "Book not found: 999",
                exception.getMessage()
        );

        verify(bookService)
                .findByIdForUser(
                        999L,
                        1L
                );
        }

    @Test
    void shouldCreateBookForAuthenticatedUser() {

        BookResponse createdBook =
                createSampleBook();

        given(
                bookService.create(
                        any(CreateBookRequest.class),
                        same(authenticatedUser)
                )
        ).willReturn(createdBook);

        CreateBookRequest request =
                new CreateBookRequest(
                        "Clean Code",
                        "Robert C. Martin",
                        2008,
                        "Software Engineering",
                        "A book about clean code.",
                        null
                );

        BookController controller =
                new BookController(bookService);

        ResponseEntity<BookResponse> response =
                controller.create(
                        request,
                        authenticatedUser
                );

        assertEquals(
                201,
                response.getStatusCode().value()
        );

        assertEquals(
                "/api/books/1",
                response.getHeaders()
                        .getLocation()
                        .toString()
        );

        assertEquals(
                "Clean Code",
                response.getBody().title()
        );

        verify(bookService)
                .create(
                        any(CreateBookRequest.class),
                        same(authenticatedUser)
                );
    }

    /**
     * Keeps an HTTP-level test for request/response serialization.
     */
    @Test
    void shouldCreateBook() throws Exception {

        BookResponse createdBook =
                createSampleBook();

        given(
                bookService.create(
                        any(CreateBookRequest.class),
                        any(User.class)
                )
        ).willReturn(createdBook);

        String requestBody = """
                {
                  "title": "Clean Code",
                  "author": "Robert C. Martin",
                  "publishedYear": 2008,
                  "genre": "Software Engineering",
                  "description": "A book about clean code.",
                  "coverUrl": null
                }
                """;

        mockMvc.perform(
                        post("/api/books")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(
                        header().string(
                                "Location",
                                "/api/books/1"
                        )
                )
                .andExpect(
                        jsonPath("$.id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.title")
                                .value("Clean Code")
                );
    }

    @Test
    void shouldRejectInvalidBook() throws Exception {

        String requestBody = """
                {
                  "title": "",
                  "author": ""
                }
                """;

        mockMvc.perform(
                        post("/api/books")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest());

        verify(bookService, never())
                .create(
                        any(CreateBookRequest.class),
                        any(User.class)
                );
    }

    @Test
    void shouldUpdateBookForAuthenticatedUser() {

        BookResponse updatedBook =
                new BookResponse(
                        1L,
                        "Clean Code Updated",
                        "Robert C. Martin",
                        2008,
                        "Software Engineering",
                        "Updated description",
                        null,
                        Instant.parse(
                                "2026-09-01T00:00:00Z"
                        ),
                        Instant.parse(
                                "2026-09-01T01:00:00Z"
                        )
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
                bookService.updateForUser(
                        1L,
                        1L,
                        request
                )
        ).willReturn(Optional.of(updatedBook));

        BookController controller =
                new BookController(bookService);

        ResponseEntity<BookResponse> response =
                controller.update(
                        1L,
                        request,
                        authenticatedUser
                );

        assertEquals(
                200,
                response.getStatusCode().value()
        );

        assertEquals(
                "Clean Code Updated",
                response.getBody().title()
        );

        verify(bookService)
                .updateForUser(
                        1L,
                        1L,
                        request
                );
    }

    @Test
    void shouldReturnNotFoundWhenBookCannotBeUpdatedByUser() {

        UpdateBookRequest request =
                new UpdateBookRequest(
                        "Unknown Book",
                        "Unknown Author",
                        null,
                        null,
                        null,
                        null
                );

        given(
                bookService.updateForUser(
                        999L,
                        1L,
                        request
                )
        ).willReturn(Optional.empty());

        BookController controller =
                new BookController(bookService);

        BookNotFoundException exception =
                assertThrows(
                        BookNotFoundException.class,
                        () -> controller.update(
                                999L,
                                request,
                                authenticatedUser
                        )
                );

        assertEquals(
                "Book not found: 999",
                exception.getMessage()
        );

        verify(bookService)
                .updateForUser(
                        999L,
                        1L,
                        request
                );
        }

    @Test
    void shouldDeleteBookForAuthenticatedUser() {

        given(
                bookService.deleteForUser(
                        1L,
                        1L
                )
        ).willReturn(true);

        BookController controller =
                new BookController(bookService);

        ResponseEntity<Void> response =
                controller.delete(
                        1L,
                        authenticatedUser
                );

        assertEquals(
                204,
                response.getStatusCode().value()
        );

        verify(bookService)
                .deleteForUser(
                        1L,
                        1L
                );
    }

    @Test
    void shouldReturnNotFoundWhenBookCannotBeDeletedByUser() {

        given(
                bookService.deleteForUser(
                        999L,
                        1L
                )
        ).willReturn(false);

        BookController controller =
                new BookController(bookService);

        BookNotFoundException exception =
                assertThrows(
                        BookNotFoundException.class,
                        () -> controller.delete(
                                999L,
                                authenticatedUser
                        )
                );

        assertEquals(
                "Book not found: 999",
                exception.getMessage()
        );

        verify(bookService)
                .deleteForUser(
                        999L,
                        1L
                );
        }

    /**
     * Creates a reusable BookResponse for controller tests.
     */
    private BookResponse createSampleBook() {

        Instant now =
                Instant.parse(
                        "2026-09-01T00:00:00Z"
                );

        return new BookResponse(
                1L,
                "Clean Code",
                "Robert C. Martin",
                2008,
                "Software Engineering",
                "A book about clean code.",
                null,
                now,
                now
        );
    }
}