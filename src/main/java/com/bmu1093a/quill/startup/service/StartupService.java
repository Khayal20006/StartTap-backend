package com.bmu1093a.quill.startup.service;

import com.bmu1093a.quill.auth.model.entity.User;
import com.bmu1093a.quill.startup.mapper.StartupMapper;
import com.bmu1093a.quill.startup.model.dto.request.StartupRequestDto;
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


    private User getCurrentUser() {
        return userLookupService.getCurrentUser();
    }


    public List<StartupResponseDto> getAllStartups() {
        List<Startup> startups = startupRepository.findAll();

        return startups.stream().map(startupMapper::toDto).toList();
    }

    public StartupResponseDto getStartupById(Long id) {

        Startup startup = startupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Startup not found"));

        return startupMapper.toDto(startup);
    }

    public StartupResponseDto createStartup(StartupRequestDto startupRequestDto) {

        Startup startup = startupMapper.toStartup(startupRequestDto);

        startup.setOwner(getCurrentUser());

        startupRepository.save(startup);


        return startupMapper.toDto(startup);

    }
}
