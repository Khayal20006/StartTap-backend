package com.bmu1093a.quill.vacancy.model.dto.response;

import com.bmu1093a.quill.vacancy.model.entity.enumeration.ApplicationStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class VacancyApplicationResponseDto {
    private Long id;
    private Long jobId;
    private String jobTitle;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;
}
