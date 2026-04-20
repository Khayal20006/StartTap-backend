package com.bmu1093a.quill.vacancy.model.dto.request;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class VacancyUpdateRequestDto {

    private String title;
    private String description;
    private BigDecimal salary;
    private Boolean isActive;
}
