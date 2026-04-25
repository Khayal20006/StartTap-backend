package com.bmu1093a.quill.vacancy.model.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class VacancyRequestDto {
    private String title;

    private String description;

    private BigDecimal salary;

    private Long startupId;
}