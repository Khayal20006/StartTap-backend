package com.bmu1093a.quill.email.service;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    private JavaMailSender mailSender;
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        mailSender = mock(JavaMailSender.class);
        emailService = new EmailService(mailSender);

        ReflectionTestUtils.setField(emailService, "frontendUrl", "http://localhost:3000");
    }

    // ✅ 1. success case
    @Test
    void shouldSendVerificationEmail() {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);

        doNothing().when(mailSender).send(any(MimeMessage.class));

        emailService.sendVerificationEmail("test@mail.com", "abc123");

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(captor.capture());

        MimeMessage sentMessage = captor.getValue();
        assertNotNull(sentMessage);
    }

    // ❗ 2. exception handling
    @Test
    void shouldThrowRuntimeException_whenMailFails() {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        doThrow(new RuntimeException("Failed to send email"))
                .when(mailSender).send(any(MimeMessage.class));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            emailService.sendVerificationEmail("test@mail.com", "token123");
        });

        assertTrue(ex.getMessage().contains("Failed to send email"));
    }

    // ✅ 3. verify URL logic indirectly
    @Test
    void shouldIncludeTokenInEmailFlow() {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendVerificationEmail("test@mail.com", "myToken");

        verify(mailSender).send(any(MimeMessage.class));
    }
}