package com.bmu1093a.quill.profile.service;

import com.bmu1093a.quill.profile.model.dto.ProfileResponseDto;
import com.bmu1093a.quill.profile.model.dto.UpdateProfileRequestDto;

public interface ProfileService {
        ProfileResponseDto getProfile();
        ProfileResponseDto updateProfile(UpdateProfileRequestDto request);
    }
