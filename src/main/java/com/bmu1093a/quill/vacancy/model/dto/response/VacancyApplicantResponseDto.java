package com.bmu1093a.quill.vacancy.model.dto.response;

import com.bmu1093a.quill.vacancy.model.entity.enumeration.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VacancyApplicantResponseDto {

    private Long applicationId;

    private Long userId;
    private String firstname;
    private String lastname;
    private String email;

    private ApplicationStatus status;
    private LocalDateTime appliedAt;
}