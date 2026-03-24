package com.bmu1093a.quill.job.model.dto.request;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class JobRequestDto {
    private String title;

    private String description;

    private BigDecimal salary;
}
