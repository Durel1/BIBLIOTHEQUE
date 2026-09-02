package com.durel.bibliotheque.exception;

/**
 * Thrown when the provided login credentials are invalid.
 */
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Invalid credentials");
    }
}
