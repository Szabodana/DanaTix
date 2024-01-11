package com.project.danatix.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;
    private final Environment env;

    public EmailServiceImpl(JavaMailSender javaMailSender, Environment env) {
        this.javaMailSender = javaMailSender;
        this.env = env;
    }

    @Override
    @Async
    public void sendHtmlEmail(String address, String subject, String content) {

        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(address);
            helper.setFrom(env.getProperty("mail.from"));
            helper.setSubject(subject);
            helper.setText(content, true);

            javaMailSender.send(message);
        }catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendVerificationEmail(String address, String token) {
        String urlEncodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);
        String content = "<html><body><h2>Welcome to danatix!</h2>" +
                "<p>Please verify your email by clicking on the link below: </p>" +
                "<a href='" + env.getProperty("app.domain") +
                "/email-verification?token=" + urlEncodedToken + "'>Verify now!</a></body></html>";

        String subject = "danatix: Verify your email address";

        sendHtmlEmail(address, subject, content);
    }
}
