package com.durel.bibliotheque.exception;

/**
 * Thrown when registration cannot continue because
 * the email or username is already registered.
 */
public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
