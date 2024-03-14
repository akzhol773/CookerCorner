package org.example.cookercorner.exceptions;

public class PasswordDontMatchException extends RuntimeException {
    public PasswordDontMatchException(String message) {
        super(message);
    }
}
