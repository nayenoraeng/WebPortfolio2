package com.project.parkrental.security.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendPwdResetEmail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        message.setFrom("keonk1614@gmail.com");

        try {
            mailSender.send(message);
            System.out.println("reset email sent to " + to);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
