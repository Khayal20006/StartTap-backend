package com.bmu1093a.quill.profile.service;

import com.bmu1093a.quill.auth.error.UserNotFoundException;
import com.bmu1093a.quill.auth.model.entity.User;
import com.bmu1093a.quill.auth.repository.UserRepository;
import com.bmu1093a.quill.file.exception.FileOperationException;
import com.bmu1093a.quill.file.model.entity.FileRecord;
import com.bmu1093a.quill.file.repo.FileRecordRepository;
import com.bmu1093a.quill.profile.model.dto.ProfileRequestDto;
import com.bmu1093a.quill.profile.model.dto.ProfileResponseDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final UserRepository userRepository;
    private final FileRecordRepository fileRecordRepository;

    public ProfileResponseDto getProfile() {
        User user = getCurrentUser();
        Optional<FileRecord> lastCv = fileRecordRepository.findFirstByUserEmailAndDeletedFalseOrderByIdDesc(user.getEmail());
        return ProfileResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .linkedinUrl(user.getLinkedinUrl())
                .githubUrl(user.getGithubUrl())
//                .role(user.getRole())
                .cvUrl(lastCv.map(FileRecord::getUrl).orElse(null))
                .cvFileName(lastCv.map(FileRecord::getOriginalFileName).orElse(null))
                .build();

    }

    @Transactional
    public ProfileResponseDto updateProfile(ProfileRequestDto request) {
        User user = getCurrentUser();
        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getUsername() != null) user.setUsername(request.getUsername());
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
        if (request.getLinkedinUrl() != null) user.setLinkedinUrl(request.getLinkedinUrl());
        if (request.getGithubUrl() != null) user.setGithubUrl(request.getGithubUrl());
        userRepository.save(user);
        return getProfile();
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new FileOperationException("UNAUTHORIZED", "Authentication required");
        }
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public ProfileResponseDto getProfileById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User Not Found"));
        Optional<FileRecord> lastCv = fileRecordRepository.findFirstByUserEmailAndDeletedFalseOrderByIdDesc(user.getEmail());

        return ProfileResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .cvUrl(lastCv.map(FileRecord::getUrl).orElse(null))
                .cvFileName(lastCv.map(FileRecord::getOriginalFileName).orElse(null)).phoneNumber(user.getPhoneNumber())
                .linkedinUrl(user.getLinkedinUrl())
                .githubUrl(user.getGithubUrl())
                .build();

    }
}
