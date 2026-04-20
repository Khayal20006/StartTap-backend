package com.bmu1093a.quill.vacancy.controller;

import com.bmu1093a.quill.vacancy.model.dto.response.VacancyApplicantResponseDto;
import com.bmu1093a.quill.vacancy.model.dto.response.VacancyApplicationResponseDto;
import com.bmu1093a.quill.vacancy.service.VacancyApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/vacancies")
@RequiredArgsConstructor
public class VacancyApplicationController {

    private final VacancyApplicationService vacancyApplicationService;

    @PostMapping("{vacancyId}/applications")
    public ResponseEntity<VacancyApplicationResponseDto> applyToVacancy(
            @PathVariable Long vacancyId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vacancyApplicationService.applyToVacancy(vacancyId));
    }

    @GetMapping("{vacancyId}/applications")
    public ResponseEntity<List<VacancyApplicantResponseDto>> getApplicationsByVacancyId(@PathVariable Long vacancyId) {

        return ResponseEntity.status(HttpStatus.OK).body(vacancyApplicationService.getApplicationsByVacancyId(vacancyId));
    }


}
