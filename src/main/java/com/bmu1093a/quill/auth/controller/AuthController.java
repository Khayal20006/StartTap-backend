package com.bmu1093a.quill.auth.controller;

import com.bmu1093a.quill.auth.model.dto.login.LoginRequestDto;
import com.bmu1093a.quill.auth.model.dto.login.LoginResponseDto;
import com.bmu1093a.quill.auth.model.dto.register.RegisterRequestDto;
import com.bmu1093a.quill.auth.service.AuthService;
import com.bmu1093a.quill.verification.service.VerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final VerificationService verificationService;

    @PostMapping("register")
    public void register(@Valid @RequestBody RegisterRequestDto dto) {
        authService.register(dto);
    }

    @PostMapping("login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        return ResponseEntity.ok(authService.login(loginRequestDto));
    }

    @GetMapping("verify")
    public ResponseEntity<LoginResponseDto> verify(@RequestParam String token) {
        return ResponseEntity.ok(verificationService.verifyAndLogin(token));
    }
}