package com.durel.bibliotheque.service;

import com.durel.bibliotheque.dto.RegisterRequest;
import com.durel.bibliotheque.dto.RegisterResponse;
import com.durel.bibliotheque.entity.User;
import com.durel.bibliotheque.repository.UserRepository;
import com.durel.bibliotheque.exception.UserAlreadyExistsException;
import com.durel.bibliotheque.dto.LoginRequest;
import com.durel.bibliotheque.dto.LoginResponse;
import com.durel.bibliotheque.exception.InvalidCredentialsException;
import com.durel.bibliotheque.security.JwtService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

/**
 * Contains business logic related to users.
 */
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    
    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * Registers a new user.
     *
     * The password is encoded before the User entity is persisted.
     */
    public RegisterResponse register(RegisterRequest request) {

        String username = request.username().trim();
        String email = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("Email is already registered");
        }

        if (userRepository.existsByUsername(username)) {
            throw new UserAlreadyExistsException(
        "Username is already registered");
        }

        String encodedPassword =
                passwordEncoder.encode(request.password());

        User user = new User(
                username,
                email,
                encodedPassword
        );

        User savedUser = userRepository.save(user);

        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getCreatedAt()
        );
    }

    /**
    * Authenticates a user with an email and password.
    */
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {

        String email = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        boolean passwordMatches =
                passwordEncoder.matches(
                        request.password(),
                        user.getPassword()
                );

        if (!passwordMatches) {
            throw new InvalidCredentialsException();
        }

        String token =
        jwtService.generateToken(user);

        return new LoginResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                token
        );
    }
}
