package com.bmu1093a.quill.vacancy.mapper;

import com.bmu1093a.quill.auth.model.entity.User;
import com.bmu1093a.quill.vacancy.model.dto.response.VacancyApplicantResponseDto;
import com.bmu1093a.quill.vacancy.model.dto.response.VacancyApplicationResponseDto;
import com.bmu1093a.quill.vacancy.model.entity.VacancyApplication;
import org.springframework.stereotype.Component;

@Component
public class VacancyApplicationMapper {

    public VacancyApplicationResponseDto toVacancyApplicationResponseDto(VacancyApplication vacancyApplication) {
        return VacancyApplicationResponseDto.builder()
                .id(vacancyApplication.getId())
                .jobId(vacancyApplication.getVacancy().getId())
                .jobTitle(vacancyApplication.getVacancy().getTitle())
                .status(vacancyApplication.getStatus())
                .appliedAt(vacancyApplication.getAppliedAt())
                .build();
    }

    public VacancyApplicantResponseDto toVacancyApplicantDtoResponse(VacancyApplication vacancyApplication) {

        User applicant = vacancyApplication.getUser();

        return VacancyApplicantResponseDto.builder()
                .applicationId(vacancyApplication.getId())
                .userId(applicant.getId())
                .firstname(applicant.getFirstName())
                .lastname(applicant.getLastName())
                .email(applicant.getEmail())
                .status(vacancyApplication.getStatus())
                .appliedAt(vacancyApplication.getAppliedAt())
                .build();
    }
}
