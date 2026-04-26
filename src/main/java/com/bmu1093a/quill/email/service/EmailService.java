package com.bmu1093a.quill.email.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    public void sendVerificationEmail(String to, String token) {


//        String link = "http://localhost:8080/api/auth/verify?token=" + token;
        String link = "http://localhost:5173/api/auth/verify?token=" + token;

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject("Verify your email");
        simpleMailMessage.setText("Click to verify: " + link);

        javaMailSender.send(simpleMailMessage);

    }


}
