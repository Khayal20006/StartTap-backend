package com.bmu1093a.quill.job.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobResponseDto {
    private Long id;

    private String title;

    private String description;

    private BigDecimal salary;

    private EmployeeResponseDto employer;

    private LocalDateTime createdAt;

    private Boolean isActive;
}
