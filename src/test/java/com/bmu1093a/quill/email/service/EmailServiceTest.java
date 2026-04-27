package com.bmu1093a.quill.email.service;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private EmailService emailService;

    private final String frontendUrl = "http://localhost:3000";

    @BeforeEach
    void setUp() {
        // Manually set the @Value field since we aren't using a full Spring context
        ReflectionTestUtils.setField(emailService, "frontendUrl", frontendUrl);
    }

    @Test
    void sendVerificationEmail_Success() throws Exception {
        // Arrange
        String to = "user@example.com";
        String token = "test-token-123";

        // MimeMessage needs a Session to be instantiated, even if null
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Act
        emailService.sendVerificationEmail(to, token);

        // Assert
        verify(javaMailSender, times(1)).createMimeMessage();
        verify(javaMailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void sendVerificationEmail_ThrowsRuntimeException_WhenMessagingExceptionOccurs() {
        // Arrange
        String to = "user@example.com";
        String token = "test-token-123";

        // Mock createMimeMessage to throw an exception or handle it via doThrow on send
        when(javaMailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));
        doThrow(new RuntimeException("SMTP Server Down")).when(javaMailSender).send(any(MimeMessage.class));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            emailService.sendVerificationEmail(to, token);
        });
    }
}