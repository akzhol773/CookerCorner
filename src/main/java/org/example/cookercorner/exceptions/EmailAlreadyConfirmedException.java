package org.example.cookercorner.exceptions;

public class EmailAlreadyConfirmedException extends RuntimeException{
    public EmailAlreadyConfirmedException(String message) {
        super(message);
    }
}
