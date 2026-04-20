package com.bmu1093a.quill.vacancy.model.dto.request;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class VacancyRequestDto {
    private String title;

    private String description;

    private BigDecimal salary;

    private Long startupId;
}