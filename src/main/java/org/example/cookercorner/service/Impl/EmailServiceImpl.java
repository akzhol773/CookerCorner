package org.example.cookercorner.service.Impl;

import com.neobis.neoauth.entities.User;
import com.neobis.neoauth.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;


@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine engine;

    public void sendConfirm(String to, String body) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(body, true);
            helper.setTo(to);
            helper.setSubject("Confirm your email");
            helper.setFrom("lorby@edu.com");
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new IllegalStateException("Failed to send email");
        }

    }

    @Override
    public void sendConfirmationMail(String link, User user){
        Context context = new Context();
        context.setVariable("confirmEmailUrl", link);
        String emailBody = engine.process("confirmation_email", context);
       sendConfirm(user.getEmail(), emailBody);
    }

    @Override
    public void sendForgotPasswordMail(String link, User user) {
        Context context = new Context();
        context.setVariable("forgotPasswordUrl", link);
        String emailBody = engine.process("forgot_password", context);
        sendReset(user.getEmail(), emailBody);

    }


    public void sendReset(String to, String body) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(body, true);
            helper.setTo(to);
            helper.setSubject("Reset your password");
            helper.setFrom("lorby@edu.com");
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new IllegalStateException("Failed to send email");
        }
    }
}
