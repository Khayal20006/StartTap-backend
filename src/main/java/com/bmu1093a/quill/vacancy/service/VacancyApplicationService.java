package com.bmu1093a.quill.vacancy.service;

import com.bmu1093a.quill.auth.model.entity.User;
import com.bmu1093a.quill.auth.repository.UserRepository;
import com.bmu1093a.quill.vacancy.mapper.VacancyApplicationMapper;
import com.bmu1093a.quill.vacancy.model.dto.response.VacancyApplicantResponseDto;
import com.bmu1093a.quill.vacancy.model.dto.response.VacancyApplicationResponseDto;
import com.bmu1093a.quill.vacancy.model.entity.Vacancy;
import com.bmu1093a.quill.vacancy.model.entity.VacancyApplication;
import com.bmu1093a.quill.vacancy.respository.VacancyApplicationRepository;
import com.bmu1093a.quill.vacancy.respository.VacancyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VacancyApplicationService {

    private final VacancyApplicationRepository vacancyApplicationRepository;
    private final VacancyRepository vacancyRepository;
    private final VacancyApplicationMapper vacancyApplicationMapper;
    private final UserRepository userRepository;
    private final UserLookupService userLookupService;

    public VacancyApplicationResponseDto applyToVacancy(
//            VacancyApplicationRequestDto vacancyApplicationRequestDto
            Long vacancyId
    ) {
        User currentUser = userLookupService.getCurrentUser();

//        Long vacancyId = vacancyApplicationRequestDto.getVacancyId();
        Long userId = currentUser.getId();

        Vacancy vacancy = vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new RuntimeException("Vacancy not found"));

        if (!vacancy.getIsActive()) {
            throw new IllegalStateException("Vacancy is not active");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean alreadyApplied = vacancyApplicationRepository
                .existsByUserIdAndVacancyId(userId, vacancyId);

        if (alreadyApplied) {
            throw new IllegalStateException("Already applied");
        }

        VacancyApplication vacancyApplication = VacancyApplication.builder()
                .user(user)
                .vacancy(vacancy)
                .build();

        vacancyApplicationRepository.save(vacancyApplication);

        return vacancyApplicationMapper.toVacancyApplicationResponseDto(vacancyApplication);

    }

    public List<VacancyApplicantResponseDto> getApplicationsByVacancyId(Long id) {

        List<VacancyApplication> vacancyApplications = vacancyApplicationRepository.findVacancyApplicationsByVacancyId(id);

        return vacancyApplications.stream().map(vacancyApplicationMapper::toVacancyApplicantDtoResponse).toList();
    }


}
