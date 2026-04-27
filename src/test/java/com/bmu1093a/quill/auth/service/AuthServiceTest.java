package com.bmu1093a.quill.auth.service;

import com.bmu1093a.quill.common.exception.UserNotFoundException;
import com.bmu1093a.quill.common.exception.WrongPasswordException;
import com.bmu1093a.quill.auth.model.dto.login.LoginRequestDto;
import com.bmu1093a.quill.auth.model.dto.register.RegisterRequestDto;
import com.bmu1093a.quill.auth.model.entity.User;
import com.bmu1093a.quill.auth.model.enumeration.Role;
import com.bmu1093a.quill.auth.repository.UserRepository;
import com.bmu1093a.quill.auth.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private RegisterRequestDto registerRequest;
    private LoginRequestDto loginRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequestDto();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@mail.com");
        registerRequest.setPassword("123456");

        loginRequest = new LoginRequestDto();
        loginRequest.setEmail("test@mail.com");
        loginRequest.setPassword("123456");
    }

    // ---------------- REGISTER TESTS ----------------

    @Test
    void register_success() {
        when(userRepository.findByEmail(registerRequest.getEmail()))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(registerRequest.getPassword()))
                .thenReturn("encodedPassword");

        when(jwtUtil.generateToken(anyString(), anyString()))
                .thenReturn("access-token");

        when(jwtUtil.generateRefreshToken(anyString(), anyString()))
                .thenReturn("refresh-token");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    user.setId(1L);
                    return user;
                });

        var result = authService.register(registerRequest);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@mail.com", result.getEmail());
        assertEquals(Role.USER, result.getRole());
        assertEquals("access-token", result.getAccessToken());
        assertEquals("refresh-token", result.getRefreshToken());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_shouldThrowException_whenEmailExists() {
        when(userRepository.findByEmail(registerRequest.getEmail()))
                .thenReturn(Optional.of(new User()));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.register(registerRequest));

        assertEquals("Email already exists", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    // ---------------- LOGIN TESTS ----------------

    @Test
    void login_success() {
        User user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@mail.com")
                .password("encodedPassword")
                .role(Role.USER)
                .build();

        when(userRepository.findByEmail(loginRequest.getEmail()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword()))
                .thenReturn(true);

        when(jwtUtil.generateToken(anyString(), anyString()))
                .thenReturn("access-token");

        when(jwtUtil.generateRefreshToken(anyString(), anyString()))
                .thenReturn("refresh-token");

        var result = authService.login(loginRequest);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("access-token", result.getAccessToken());
        assertEquals("refresh-token", result.getRefreshToken());
    }

    @Test
    void login_shouldThrowUserNotFoundException() {
        when(userRepository.findByEmail(loginRequest.getEmail()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> authService.login(loginRequest));
    }

    @Test
    void login_shouldThrowWrongPasswordException() {
        User user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@mail.com")
                .password("encodedPassword")
                .role(Role.USER)
                .build();

        when(userRepository.findByEmail(loginRequest.getEmail()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(anyString(), anyString()))
                .thenReturn(false);

        assertThrows(WrongPasswordException.class,
                () -> authService.login(loginRequest));
    }
}