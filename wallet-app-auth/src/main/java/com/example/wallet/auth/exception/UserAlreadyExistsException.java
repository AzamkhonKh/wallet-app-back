package com.example.wallet.auth.exception;

// Remove Spring web annotations and imports
// import org.springframework.http.HttpStatus; <-- REMOVE
// import org.springframework.web.bind.annotation.ResponseStatus; <-- REMOVE

// @ResponseStatus(HttpStatus.CONFLICT) <-- REMOVE
public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}