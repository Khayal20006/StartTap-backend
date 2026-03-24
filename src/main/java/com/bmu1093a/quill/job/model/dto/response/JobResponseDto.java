package com.bmu1093a.quill.job.model.dto.response;

import com.bmu1093a.quill.auth.model.entity.User;
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

    private User employer;

    private LocalDateTime createdAt;

    private Boolean isActive;
}
