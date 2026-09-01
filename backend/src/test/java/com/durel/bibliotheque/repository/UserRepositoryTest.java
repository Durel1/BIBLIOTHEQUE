package com.durel.bibliotheque.repository;

import com.durel.bibliotheque.entity.User;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindUserByEmail() {

        User user = new User(
                "jordan",
                "jordan@example.com",
                "hashed-password"
        );

        userRepository.save(user);

        Optional<User> result =
                userRepository.findByEmail("jordan@example.com");

        assertTrue(result.isPresent());
    }

    @Test
    void shouldCheckIfEmailExists() {

        User user = new User(
                "jordan",
                "jordan@example.com",
                "hashed-password"
        );

        userRepository.save(user);

        assertTrue(
                userRepository.existsByEmail("jordan@example.com")
        );

        assertFalse(
                userRepository.existsByEmail("unknown@example.com")
        );
    }

    @Test
    void shouldCheckIfUsernameExists() {

        User user = new User(
                "jordan",
                "jordan@example.com",
                "hashed-password"
        );

        userRepository.save(user);

        assertTrue(
                userRepository.existsByUsername("jordan")
        );

        assertFalse(
                userRepository.existsByUsername("unknown")
        );
    }
}
