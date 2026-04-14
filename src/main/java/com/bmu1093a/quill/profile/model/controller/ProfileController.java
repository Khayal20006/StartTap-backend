package com.bmu1093a.quill.profile.model.controller;

import com.bmu1093a.quill.profile.model.dto.ProfileResponseDto;
import com.bmu1093a.quill.profile.model.dto.ProfileRequestDto;
import com.bmu1093a.quill.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/me")
    public ResponseEntity<ProfileResponseDto> getProfile() {
        return ResponseEntity.ok(profileService.getProfile());
    }

    @PutMapping("/me")
    public ResponseEntity<ProfileResponseDto> updateProfile(@RequestBody ProfileRequestDto request) {
        return ResponseEntity.ok(profileService.updateProfile(request));
    }
}