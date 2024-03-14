package org.example.cookercorner.service;


import com.neobis.neoauth.entities.User;

public interface EmailService {
    public void sendConfirmationMail(String link, User user);
    public void sendForgotPasswordMail(String link, User user);
}
