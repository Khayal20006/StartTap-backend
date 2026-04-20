package com.bmu1093a.quill.startup.mapper;

import com.bmu1093a.quill.auth.model.entity.User;
import com.bmu1093a.quill.startup.model.dto.request.StartupRequestDto;
import com.bmu1093a.quill.startup.model.dto.respone.StartupResponseDto;
import com.bmu1093a.quill.startup.model.entity.Startup;
import com.bmu1093a.quill.startup.model.entity.dto.response.OwnerResponseDto;
import org.springframework.stereotype.Component;

@Component
public class StartupMapper {

    public StartupResponseDto toDto(Startup startup) {
        if (startup == null) return null;

        return StartupResponseDto.builder()
                .id(startup.getId())
                .name(startup.getName())
                .tagline(startup.getTagline())
                .description(startup.getDescription())
                .category(startup.getCategory())
                .stage(startup.getStage())
                .website(startup.getWebsite())
                .isActive(startup.getIsActive())
                .createdAt(startup.getCreatedAt())
                .owner(toOwnerDto(startup.getOwner()))
                .build();
    }

    public Startup toStartup(StartupRequestDto startupRequestDto) {
        return Startup.builder()
                .name(startupRequestDto.getName())
                .tagline(startupRequestDto.getTagline())
                .description(startupRequestDto.getDescription())
                .category(startupRequestDto.getCategory())
                .stage(startupRequestDto.getStage())
                .website(startupRequestDto.getWebsite())
                .build();

    }

    public OwnerResponseDto toOwnerDto(User user) {
        if (user == null) return null;

        return OwnerResponseDto.builder()
                .id(user.getId())
                .firstname(user.getFirstName())
                .lastname(user.getLastName())
                .email(user.getEmail())
                .build();
    }
}