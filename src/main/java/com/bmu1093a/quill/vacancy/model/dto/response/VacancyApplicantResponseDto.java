package com.bmu1093a.quill.vacancy.model.dto.response;

import com.bmu1093a.quill.vacancy.model.entity.enumeration.ApplicationStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class VacancyApplicantResponseDto {

    private Long applicationId;

    private Long userId;
    private String firstname;
    private String lastname;
    private String email;

    private ApplicationStatus status;
    private LocalDateTime appliedAt;
}