package org.example.cookercorner.exceptions;

public class UserNotEnabledException extends RuntimeException{
    public UserNotEnabledException(String message) {
        super(message);
    }
}
