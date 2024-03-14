package org.example.cookercorner.exceptions;

public class UserRoleNotFoundException extends RuntimeException{
    public UserRoleNotFoundException(String message) {
        super(message);
    }
}
