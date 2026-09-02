package com.durel.bibliotheque.security;

import com.durel.bibliotheque.entity.User;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    private static final String TEST_SECRET =
            "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=";

    @Test
    void shouldGenerateAndReadToken() throws Exception {

        JwtService jwtService =
                new JwtService(TEST_SECRET, 3600000);

        User user = new User(
                "durel",
                "durel@example.com",
                "encoded-password"
        );

        setUserId(user, 42L);

        String token =
                jwtService.generateToken(user);

        assertTrue(jwtService.isTokenValid(token));
        assertEquals(
                42L,
                jwtService.extractUserId(token)
        );
    }

    @Test
    void shouldRejectInvalidToken() {

        JwtService jwtService =
                new JwtService(TEST_SECRET, 3600000);

        assertFalse(
                jwtService.isTokenValid(
                        "this-is-not-a-valid-token"
                )
        );
    }

    /**
     * JPA normally assigns the ID.
     * Reflection is used here only because this is a unit test
     * without a database.
     */
    private void setUserId(User user, Long id)
            throws Exception {

        Field idField =
                User.class.getDeclaredField("id");

        idField.setAccessible(true);
        idField.set(user, id);
    }
}
