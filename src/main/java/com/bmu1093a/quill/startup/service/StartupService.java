package com.bmu1093a.quill.startup.service;

import com.bmu1093a.quill.auth.model.entity.User;
import com.bmu1093a.quill.startup.mapper.StartupMapper;
import com.bmu1093a.quill.startup.model.dto.request.StartupRequestDto;
import com.bmu1093a.quill.startup.model.dto.request.StartupUpdateRequestDto;
import com.bmu1093a.quill.startup.model.dto.respone.StartupResponseDto;
import com.bmu1093a.quill.startup.model.entity.Startup;
import com.bmu1093a.quill.startup.repository.StartupRepository;
import com.bmu1093a.quill.vacancy.service.UserLookupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StartupService {

    private final StartupRepository startupRepository;
    private final UserLookupService userLookupService;
    private final StartupMapper startupMapper;


    private User getCurrentUserOrNull() {
        try {
            return userLookupService.getCurrentUser();
        } catch (Exception e) {
            return null;
        }
    }


    public List<StartupResponseDto> getAllStartups() {
        List<Startup> startups = startupRepository.findAll();

        return startups.stream().map(startupMapper::toDto).toList();
    }

    public StartupResponseDto getStartupById(Long id) {

        Startup startup = startupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Startup not found"));

        StartupResponseDto startupResponseDto = startupMapper.toDto(startup);

        User currentUser = getCurrentUserOrNull();

        boolean isOwner = false;

        if (currentUser != null) {
            isOwner = startup.getOwner().getId().equals(currentUser.getId());
        }

        startupResponseDto.setIsOwner(isOwner);

        return startupResponseDto;
    }

    public StartupResponseDto createStartup(StartupRequestDto startupRequestDto) {

        Startup startup = startupMapper.toStartup(startupRequestDto);

        startup.setOwner(getCurrentUserOrNull());

        startupRepository.save(startup);


        return startupMapper.toDto(startup);

    }

    public StartupResponseDto updateStartup(Long id, StartupUpdateRequestDto startupUpdateRequestDto) {
        Startup startup = startupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Startup not found"));

        startup.setName(startupUpdateRequestDto.getName());
        startup.setTagline(startupUpdateRequestDto.getTagline());
        startup.setDescription(startupUpdateRequestDto.getDescription());
        startup.setCategory(startupUpdateRequestDto.getCategory());
        startup.setStage(startupUpdateRequestDto.getStage());
        startup.setWebsite(startupUpdateRequestDto.getWebsite());
        startup.setIsActive(startupUpdateRequestDto.getIsActive());


        startupRepository.save(startup);

        User currentUser = getCurrentUserOrNull();

        StartupResponseDto startupResponseDto = startupMapper.toDto(startup);

        boolean isOwner = false;

        if (currentUser != null) {
            isOwner = startup.getOwner().getId().equals(currentUser.getId());
        }

        startupResponseDto.setIsOwner(isOwner);

        return startupResponseDto;
    }

    public List<StartupResponseDto> getMyStartups() {
        return startupRepository.findByOwner(getCurrentUserOrNull())
                .stream().map(startupMapper::toDto).toList();
    }
}
