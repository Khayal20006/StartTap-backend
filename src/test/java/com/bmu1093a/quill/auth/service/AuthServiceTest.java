package com.bmu1093a.quill.auth.service;

import com.bmu1093a.quill.auth.error.UserNotFoundException;
import com.bmu1093a.quill.auth.error.WrongPasswordException;
import com.bmu1093a.quill.auth.model.dto.login.LoginRequestDto;
import com.bmu1093a.quill.auth.model.dto.login.LoginResponseDto;
import com.bmu1093a.quill.auth.model.dto.register.RegisterRequestDto;
import com.bmu1093a.quill.auth.model.entity.User;
import com.bmu1093a.quill.auth.model.enumeration.Role;
import com.bmu1093a.quill.auth.repository.UserRepository;
import com.bmu1093a.quill.auth.util.JwtUtil;
import com.bmu1093a.quill.email.service.EmailService;
import com.bmu1093a.quill.verification.service.VerificationService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private VerificationService verificationService;

    @Mock
    private EmailService emailService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    // ─────────────────────────────────────────────
    // REGISTER TESTS
    // ─────────────────────────────────────────────

    @Test
    void register_shouldSaveUser_andSendEmail() {
        RegisterRequestDto dto = new RegisterRequestDto("isa", "isa@mail.com", "123");

        when(userRepository.findByEmail(dto.getEmail()))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(dto.getPassword()))
                .thenReturn("encodedPass");

        when(verificationService.createToken(any(User.class)))
                .thenReturn("verification-token");

        authService.register(dto);

        verify(userRepository).save(any(User.class));
        verify(emailService).sendVerificationEmail("isa@mail.com", "verification-token");
    }

    @Test
    void register_shouldThrowException_whenEmailExists() {
        RegisterRequestDto dto = new RegisterRequestDto("isa", "isa@mail.com", "123");

        when(userRepository.findByEmail(dto.getEmail()))
                .thenReturn(Optional.of(new User()));

        assertThrows(RuntimeException.class, () -> authService.register(dto));

        verify(userRepository, never()).save(any());
    }

    // ─────────────────────────────────────────────
    // LOGIN TESTS
    // ─────────────────────────────────────────────

    @Test
    void login_shouldReturnTokens_whenCredentialsValid() {
        LoginRequestDto dto = new LoginRequestDto("isa@mail.com", "123");

        User user = User.builder()
                .id(1L)
                .email("isa@mail.com")
                .username("isa")
                .password("encodedPass")
                .role(Role.USER)
                .enabled(true)
                .build();

        when(userRepository.findByEmail(dto.getEmail()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("123", "encodedPass"))
                .thenReturn(true);

        when(jwtUtil.generateToken(any(), any()))
                .thenReturn("access-token");

        when(jwtUtil.generateRefreshToken(any(), any()))
                .thenReturn("refresh-token");

        LoginResponseDto response = authService.login(dto);

        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals("isa@mail.com", response.getEmail());
        assertEquals("isa", response.getUsername());
    }

    @Test
    void login_shouldThrowUserNotFound() {
        LoginRequestDto dto = new LoginRequestDto("isa@mail.com", "123");

        when(userRepository.findByEmail(dto.getEmail()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> authService.login(dto));
    }

    @Test
    void login_shouldThrowWrongPassword() {
        LoginRequestDto dto = new LoginRequestDto("isa@mail.com", "123");

        User user = User.builder()
                .email("isa@mail.com")
                .password("encodedPass")
                .enabled(true)
                .build();

        when(userRepository.findByEmail(dto.getEmail()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("123", "encodedPass"))
                .thenReturn(false);

        assertThrows(WrongPasswordException.class, () -> authService.login(dto));
    }

    @Test
    void login_shouldThrow_whenUserNotEnabled() {
        LoginRequestDto dto = new LoginRequestDto("isa@mail.com", "123");

        User user = User.builder()
                .email("isa@mail.com")
                .password("encodedPass")
                .enabled(false)
                .build();

        when(userRepository.findByEmail(dto.getEmail()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("123", "encodedPass"))
                .thenReturn(true);

        assertThrows(RuntimeException.class, () -> authService.login(dto));
    }
}