package com.durel.bibliotheque.controller;

import com.durel.bibliotheque.dto.RegisterResponse;
import com.durel.bibliotheque.exception.UserAlreadyExistsException;
import com.durel.bibliotheque.service.UserService;
import com.durel.bibliotheque.dto.LoginResponse;
import com.durel.bibliotheque.exception.InvalidCredentialsException;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void shouldRegisterUser() throws Exception {

        RegisterResponse response =
                new RegisterResponse(
                        1L,
                        "durel",
                        "durel@example.com",
                        Instant.parse("2026-09-02T00:00:00Z")
                );

        given(userService.register(any()))
                .willReturn(response);

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "username": "durel",
                                          "email": "durel@example.com",
                                          "password": "Bonjour123!"
                                        }
                                        """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("durel"))
                .andExpect(
                        jsonPath("$.email")
                                .value("durel@example.com")
                )
                .andExpect(
                        jsonPath("$.password")
                                .doesNotExist()
                );
    }

    @Test
    void shouldRejectInvalidRegistrationRequest()
            throws Exception {

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "username": "a",
                                          "email": "not-an-email",
                                          "password": "123"
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnConflictWhenUserAlreadyExists()
            throws Exception {

        given(userService.register(any()))
                .willThrow(
                        new UserAlreadyExistsException(
                                "Email is already registered"
                        )
                );

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "username": "durel",
                                          "email": "durel@example.com",
                                          "password": "Bonjour123!"
                                        }
                                        """)
                )
                .andExpect(status().isConflict());
    }

        @Test
        void shouldLoginUser() throws Exception {

        LoginResponse response =
                new LoginResponse(
                        1L,
                        "durel",
                        "durel@example.com"
                );

        given(userService.login(any()))
                .willReturn(response);

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                        "email": "durel@example.com",
                                        "password": "Bonjour123!"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("durel"))
                .andExpect(
                        jsonPath("$.email")
                                .value("durel@example.com")
                )
                .andExpect(
                        jsonPath("$.password")
                                .doesNotExist()
                );
        }

        @Test
        void shouldReturnUnauthorizedForInvalidCredentials()
                throws Exception {

        given(userService.login(any()))
                .willThrow(
                        new InvalidCredentialsException()
                );

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                        "email": "durel@example.com",
                                        "password": "WrongPassword"
                                        }
                                        """)
                )
                .andExpect(status().isUnauthorized());
        }

        @Test
        void shouldRejectInvalidLoginRequest()
                throws Exception {

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                        "email": "not-an-email",
                                        "password": ""
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());
        }
}
