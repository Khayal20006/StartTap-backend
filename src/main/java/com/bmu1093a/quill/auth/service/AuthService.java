package com.bmu1093a.quill.auth.service;

import com.bmu1093a.quill.common.exception.UserNotFoundException;
import com.bmu1093a.quill.common.exception.WrongPasswordException;
import com.bmu1093a.quill.auth.model.dto.login.LoginRequestDto;
import com.bmu1093a.quill.auth.model.dto.login.LoginResponseDto;
import com.bmu1093a.quill.auth.model.dto.register.RegisterRequestDto;
import com.bmu1093a.quill.auth.model.enumeration.Role;
import com.bmu1093a.quill.auth.model.entity.User;
import com.bmu1093a.quill.auth.repository.UserRepository;
import com.bmu1093a.quill.auth.util.JwtUtil;
import com.bmu1093a.quill.common.exception.EmailAlreadyExistsException;
import com.bmu1093a.quill.email.service.EmailService;
import com.bmu1093a.quill.verification.service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationService verificationService;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;

    public void register(RegisterRequestDto registerRequestDto) {
        if (userRepository.findByEmail(registerRequestDto.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        User user = User.builder()
                .username(registerRequestDto.getUsername())
                .email(registerRequestDto.getEmail())
                .password(passwordEncoder.encode(registerRequestDto.getPassword()))
                .role(Role.USER).build();

        userRepository.save(user);

        String token = verificationService.createToken(user);

        emailService.sendVerificationEmail(user.getEmail(), token);


    }

    public  LoginResponseDto login(LoginRequestDto loginRequestDto) {
        User user = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new WrongPasswordException("Wrong password");
        }

        if (!user.isEnabled()) {
            throw new RuntimeException("Email not verified");
        }

        String accessToken = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail(), user.getRole().name());

        return new LoginResponseDto(user.getId(), user.getUsername(),
                user.getEmail(), user.getRole(), accessToken, refreshToken,
                "Login ugurlu oldu");
    }

}
