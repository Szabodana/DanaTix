package com.project.danatix.services;

import jakarta.mail.MessagingException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public interface EmailService {
    @Async
    void sendHtmlEmail(String address, String subject, String content) throws MessagingException;

    void sendVerificationEmail(String address, String token);
}
