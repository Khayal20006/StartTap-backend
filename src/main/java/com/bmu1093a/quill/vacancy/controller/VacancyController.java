package com.bmu1093a.quill.vacancy.controller;

import com.bmu1093a.quill.vacancy.model.dto.request.VacancyRequestDto;
import com.bmu1093a.quill.vacancy.model.dto.request.VacancyUpdateRequestDto;
import com.bmu1093a.quill.vacancy.model.dto.response.VacancyResponseDto;
import com.bmu1093a.quill.vacancy.service.VacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class VacancyController {

    private final VacancyService vacancyService;

    @PostMapping
    public VacancyResponseDto createVacancy(@RequestBody VacancyRequestDto vacancyRequestDto) {
        return vacancyService.createVacancy(vacancyRequestDto);
    }

    @PutMapping("vacancies/{id}")
    public VacancyResponseDto updateVacancy(@PathVariable Long id, @RequestBody VacancyUpdateRequestDto vacancyUpdateRequestDto) {

        return vacancyService.updateVacancy(id, vacancyUpdateRequestDto);
    }

    @GetMapping("vacancies/{id}")
    public VacancyResponseDto getVacancy(@PathVariable Long id) {
        return vacancyService.getVacancy(id);
    }

    @GetMapping("startups/{startupId}/vacancies")
    public List<VacancyResponseDto> getVacanciesByStartupId(@PathVariable Long startupId) {

        return vacancyService.getVacancyByStartupId(startupId);
    }


    @GetMapping("vacancies")
    public List<VacancyResponseDto> getAllVacancies() {
        return vacancyService.getAllVacancies();
    }
}
