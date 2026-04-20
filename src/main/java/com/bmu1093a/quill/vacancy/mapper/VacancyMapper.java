package com.bmu1093a.quill.vacancy.mapper;

import com.bmu1093a.quill.startup.mapper.StartupMapper;
import com.bmu1093a.quill.vacancy.model.dto.response.VacancyResponseDto;
import com.bmu1093a.quill.vacancy.model.entity.Vacancy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VacancyMapper {

    private final StartupMapper startupMapper;

    public VacancyResponseDto toDto(Vacancy vacancy) {
        if (vacancy == null) return null;

        return VacancyResponseDto.builder()
                .id(vacancy.getId())
                .title(vacancy.getTitle())
                .description(vacancy.getDescription())
                .salary(vacancy.getSalary())
                .startup(startupMapper.toDto(vacancy.getStartup()))
                .createdAt(vacancy.getCreatedAt())
                .isActive(vacancy.getIsActive())
                .build();
    }
}