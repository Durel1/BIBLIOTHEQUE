package com.durel.bibliotheque.service;

import com.durel.bibliotheque.dto.RegisterRequest;
import com.durel.bibliotheque.dto.RegisterResponse;
import com.durel.bibliotheque.entity.User;
import com.durel.bibliotheque.repository.UserRepository;
import com.durel.bibliotheque.exception.UserAlreadyExistsException;
import com.durel.bibliotheque.dto.LoginRequest;
import com.durel.bibliotheque.dto.LoginResponse;
import com.durel.bibliotheque.exception.InvalidCredentialsException;

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

import java.util.Optional;

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
                UserAlreadyExistsException.class,
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
                UserAlreadyExistsException.class,
                () -> userService.register(request)
        );
    }

        @Test
        void shouldLoginWithValidCredentials() {

        LoginRequest request = new LoginRequest(
                "  Durel@Example.com  ",
                "Bonjour123!"
        );

        User user = new User(
                "durel",
                "durel@example.com",
                "encoded-password"
        );

        given(userRepository.findByEmail("durel@example.com"))
                .willReturn(Optional.of(user));

        given(
                passwordEncoder.matches(
                        "Bonjour123!",
                        "encoded-password"
                )
        ).willReturn(true);

        LoginResponse response =
                userService.login(request);

        assertEquals("durel", response.username());
        assertEquals(
                "durel@example.com",
                response.email()
        );

        verify(passwordEncoder)
                .matches(
                        "Bonjour123!",
                        "encoded-password"
                );
        }

        @Test
        void shouldRejectUnknownEmail() {

        LoginRequest request = new LoginRequest(
                "unknown@example.com",
                "Bonjour123!"
        );

        given(
                userRepository.findByEmail(
                        "unknown@example.com"
                )
        ).willReturn(Optional.empty());

        assertThrows(
                InvalidCredentialsException.class,
                () -> userService.login(request)
        );
        }

        @Test
        void shouldRejectInvalidPassword() {

        LoginRequest request = new LoginRequest(
                "durel@example.com",
                "WrongPassword"
        );

        User user = new User(
                "durel",
                "durel@example.com",
                "encoded-password"
        );

        given(
                userRepository.findByEmail(
                        "durel@example.com"
                )
        ).willReturn(Optional.of(user));

        given(
                passwordEncoder.matches(
                        "WrongPassword",
                        "encoded-password"
                )
        ).willReturn(false);

        assertThrows(
                InvalidCredentialsException.class,
                () -> userService.login(request)
        );
        }
}
