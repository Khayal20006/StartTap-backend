package com.bmu1093a.quill.verification.service;


import com.bmu1093a.quill.auth.model.entity.User;
import com.bmu1093a.quill.verification.model.entity.VerificationToken;
import com.bmu1093a.quill.verification.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationService {

    private final VerificationTokenRepository verificationTokenRepository;

    public String createToken(User user) {

        String token = UUID.randomUUID().toString();

        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .user(user)
                .expiry(LocalDateTime.now().plusHours(24))
                .build();

        verificationTokenRepository.save(verificationToken);

        return token;

    }

    public void verify(String token) {

        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid Token"));

        if (verificationToken.getExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token Expired");
        }

        User user = verificationToken.getUser();

        user.setEnabled(true);

        verificationTokenRepository.delete(verificationToken);
    }


}
