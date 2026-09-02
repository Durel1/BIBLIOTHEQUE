package com.durel.bibliotheque.controller;

import com.durel.bibliotheque.dto.RegisterRequest;
import com.durel.bibliotheque.dto.RegisterResponse;
import com.durel.bibliotheque.exception.UserAlreadyExistsException;
import com.durel.bibliotheque.service.UserService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes authentication-related HTTP endpoints.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Registers a new application user.
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        try {

            RegisterResponse response =
                    userService.register(request);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);

        } catch (UserAlreadyExistsException exception) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .build();
        }
    }
}
