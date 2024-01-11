package com.project.danatix;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.TestPropertySource;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.project.danatix.services.EmailService;
import com.project.danatix.services.EmailServiceImpl;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class EmailServiceUnitTests {

    private EmailService emailService;

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private Environment environment;

    @Captor
    private ArgumentCaptor<MimeMessage> messageCaptor;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        emailService = new EmailServiceImpl(javaMailSender, environment);
        messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
    }

    @Test
    public void sendHtmlEmail() throws MessagingException {
        when(environment.getProperty("mail.from")).thenReturn("Dana@ticket.com");
        when(javaMailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));

        emailService.sendHtmlEmail(
                "test@mail.com",
                "Test email",
                "<html><body><h1>This is test mail</h1></body></html>"
                );

        verify(javaMailSender).send(messageCaptor.capture());

        MimeMessage capturedMessage = messageCaptor.getValue();

        InternetAddress recipient = (InternetAddress) capturedMessage.getRecipients(Message.RecipientType.TO)[0];

        assertEquals("test@mail.com", recipient.getAddress());
        assertEquals("Test email", capturedMessage.getSubject());

        //Really wanted to check text of email as well but couldn't get it out of MimeMessage no matter what I tried
        //assertEquals("<html><body><h1>This is test mail</h1></body></html>", contentText);
    }

    @Test
    public void sendVerificationEmail() throws MessagingException {
        when(environment.getProperty("mail.from")).thenReturn("Dana@ticket.com");
        when(javaMailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));

        emailService.sendVerificationEmail(
                "test@mail.com",
                "this-is-token"
        );

        verify(javaMailSender).send(messageCaptor.capture());

        MimeMessage capturedMessage = messageCaptor.getValue();

        InternetAddress recipient = (InternetAddress) capturedMessage.getRecipients(Message.RecipientType.TO)[0];

        assertEquals("test@mail.com", recipient.getAddress());
        assertEquals("danatix: Verify your email address", capturedMessage.getSubject());
    }
}
