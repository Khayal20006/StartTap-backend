package com.bmu1093a.quill.verification.service;

import com.bmu1093a.quill.auth.model.dto.login.LoginResponseDto;
import com.bmu1093a.quill.auth.model.entity.User;
import com.bmu1093a.quill.auth.model.enumeration.Role;
import com.bmu1093a.quill.auth.util.JwtUtil;
import com.bmu1093a.quill.verification.model.entity.VerificationToken;
import com.bmu1093a.quill.verification.repository.VerificationTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerificationServiceTest {

    @Mock
    private VerificationTokenRepository verificationTokenRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private VerificationService verificationService;

    private User mockUser;
    private final String testToken = "sample-uuid-token";

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .username("tester")
                .role(Role.USER) // Assuming an Enum exists
                .enabled(false)
                .build();
    }

    @Test
    void createToken_ShouldSaveAndReturnToken() {
        // Act
        String token = verificationService.createToken(mockUser);

        // Assert
        assertNotNull(token);
        verify(verificationTokenRepository, times(1)).save(any(VerificationToken.class));
    }

    @Test
    void verifyAndLogin_Success() {
        // Arrange
        VerificationToken verificationToken = VerificationToken.builder()
                .token(testToken)
                .user(mockUser)
                .expiry(LocalDateTime.now().plusHours(1))
                .build();

        when(verificationTokenRepository.findByToken(testToken)).thenReturn(Optional.of(verificationToken));
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken(anyString(), anyString())).thenReturn("refresh-token");

        // Act
        LoginResponseDto response = verificationService.verifyAndLogin(testToken);

        // Assert
        assertTrue(mockUser.isEnabled());
        assertEquals("access-token", response.getAccessToken());
        assertEquals("Email verified and logged in", response.getMessage());

        verify(verificationTokenRepository).delete(verificationToken);
    }

    @Test
    void verifyAndLogin_ThrowsException_WhenTokenNotFound() {
        // Arrange
        when(verificationTokenRepository.findByToken("invalid")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                verificationService.verifyAndLogin("invalid")
        );
        assertEquals("Invalid Token", exception.getMessage());
    }

    @Test
    void verifyAndLogin_ThrowsException_WhenTokenExpired() {
        // Arrange
        VerificationToken expiredToken = VerificationToken.builder()
                .token(testToken)
                .user(mockUser)
                .expiry(LocalDateTime.now().minusHours(1)) // Expired 1 hour ago
                .build();

        when(verificationTokenRepository.findByToken(testToken)).thenReturn(Optional.of(expiredToken));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                verificationService.verifyAndLogin(testToken)
        );
        assertEquals("Token Expired", exception.getMessage());
    }
}