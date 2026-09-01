package com.durel.bibliotheque.controller;

import com.durel.bibliotheque.dto.BookResponse;
import com.durel.bibliotheque.dto.CreateBookRequest;
import com.durel.bibliotheque.service.BookService;
import com.durel.bibliotheque.dto.UpdateBookRequest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.BDDMockito.given;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @Test
    void shouldReturnAllBooks() throws Exception {

        BookResponse book = createSampleBook();

        given(bookService.findAll())
                .willReturn(List.of(book));

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Clean Code"))
                .andExpect(jsonPath("$[0].author").value("Robert C. Martin"));
    }

    @Test
    void shouldReturnBookById() throws Exception {

        BookResponse book = createSampleBook();

        given(bookService.findById(1L))
                .willReturn(Optional.of(book));

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Clean Code"));
    }

    @Test
    void shouldReturnNotFoundWhenBookDoesNotExist() throws Exception {

        given(bookService.findById(999L))
                .willReturn(Optional.empty());

        mockMvc.perform(get("/api/books/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateBook() throws Exception {

        BookResponse createdBook = createSampleBook();

        given(bookService.create(any(CreateBookRequest.class)))
                .willReturn(createdBook);

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

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/books/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Clean Code"));
    }

    @Test
    void shouldRejectInvalidBook() throws Exception {

        String requestBody = """
                {
                  "title": "",
                  "author": ""
                }
                """;

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(bookService, never())
                .create(any(CreateBookRequest.class));
    }

    private BookResponse createSampleBook() {

        Instant now = Instant.parse("2026-09-01T00:00:00Z");

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

    @Test
    void shouldUpdateBook() throws Exception {

        BookResponse updatedBook = new BookResponse(
                1L,
                "Clean Code Updated",
                "Robert C. Martin",
                2008,
                "Software Engineering",
                "Updated description",
                null,
                Instant.parse("2026-09-01T00:00:00Z"),
                Instant.parse("2026-09-01T01:00:00Z")
        );

        given(bookService.update(
                org.mockito.ArgumentMatchers.eq(1L),
                any(UpdateBookRequest.class)))
                .willReturn(Optional.of(updatedBook));

        String requestBody = """
                {
                "title": "Clean Code Updated",
                "author": "Robert C. Martin",
                "publishedYear": 2008,
                "genre": "Software Engineering",
                "description": "Updated description",
                "coverUrl": null
                }
                """;

        mockMvc.perform(put("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title")
                        .value("Clean Code Updated"));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingMissingBook()
            throws Exception {

        given(bookService.update(
                org.mockito.ArgumentMatchers.eq(999L),
                any(UpdateBookRequest.class)))
                .willReturn(Optional.empty());

        String requestBody = """
                {
                "title": "Unknown Book",
                "author": "Unknown Author"
                }
                """;

        mockMvc.perform(put("/api/books/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteBook() throws Exception {

        given(bookService.delete(1L))
                .willReturn(true);

        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnNotFoundWhenDeletingMissingBook()
            throws Exception {

        given(bookService.delete(999L))
                .willReturn(false);

        mockMvc.perform(delete("/api/books/999"))
                .andExpect(status().isNotFound());
    }
}

