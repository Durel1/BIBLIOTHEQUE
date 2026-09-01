package com.durel.bibliotheque.repository;

import com.durel.bibliotheque.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Provides database access operations for User entities.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by email.
     * This will later be used during authentication.
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks whether an email is already registered.
     */
    boolean existsByEmail(String email);

    /**
     * Checks whether a username is already registered.
     */
    boolean existsByUsername(String username);
}
