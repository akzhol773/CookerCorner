package org.example.cookercorner.service;


import org.example.cookercorner.entities.User;

public interface EmailService {
    public void sendConfirmationMail(String link, User user);

}
