package com.bmu1093a.quill.job.model.dto.response;

import com.bmu1093a.quill.job.model.entity.enumeration.ApplicationStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class JobApplicationResponseDto {
    private Long id;
    private Long jobId;
    private String jobTitle;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;
}
