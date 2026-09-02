package com.durel.bibliotheque.service;

import com.durel.bibliotheque.dto.RegisterRequest;
import com.durel.bibliotheque.dto.RegisterResponse;
import com.durel.bibliotheque.entity.User;
import com.durel.bibliotheque.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldRegisterUserWithEncodedPassword() {

        RegisterRequest request = new RegisterRequest(
                "  durel  ",
                "  Durel@Example.com  ",
                "Bonjour123!"
        );

        given(userRepository.existsByEmail("durel@example.com"))
                .willReturn(false);

        given(userRepository.existsByUsername("durel"))
                .willReturn(false);

        given(passwordEncoder.encode("Bonjour123!"))
                .willReturn("encoded-password");

        given(userRepository.save(any(User.class)))
                .willAnswer(invocation ->
                        invocation.getArgument(0)
                );

        RegisterResponse response =
                userService.register(request);

        assertEquals("durel", response.username());
        assertEquals(
                "durel@example.com",
                response.email()
        );

        verify(passwordEncoder)
                .encode("Bonjour123!");

        verify(userRepository)
                .save(any(User.class));
    }

    @Test
    void shouldRejectAlreadyRegisteredEmail() {

        RegisterRequest request = new RegisterRequest(
                "durel",
                "durel@example.com",
                "Bonjour123!"
        );

        given(userRepository.existsByEmail("durel@example.com"))
                .willReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> userService.register(request)
        );
    }

    @Test
    void shouldRejectAlreadyRegisteredUsername() {

        RegisterRequest request = new RegisterRequest(
                "durel",
                "durel@example.com",
                "Bonjour123!"
        );

        given(userRepository.existsByEmail("durel@example.com"))
                .willReturn(false);

        given(userRepository.existsByUsername("durel"))
                .willReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> userService.register(request)
        );
    }
}
