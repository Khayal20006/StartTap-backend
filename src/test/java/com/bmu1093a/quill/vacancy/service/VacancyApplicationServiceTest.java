package com.bmu1093a.quill.vacancy.service;

import com.bmu1093a.quill.auth.model.entity.User;
import com.bmu1093a.quill.common.exception.AlreadyAppliedException;
import com.bmu1093a.quill.common.exception.ResourceNotFoundException;
import com.bmu1093a.quill.common.exception.VacancyNotActiveException;
import com.bmu1093a.quill.vacancy.mapper.VacancyApplicationMapper;
import com.bmu1093a.quill.vacancy.model.dto.response.VacancyApplicantResponseDto;
import com.bmu1093a.quill.vacancy.model.dto.response.VacancyApplicationResponseDto;
import com.bmu1093a.quill.vacancy.model.entity.Vacancy;
import com.bmu1093a.quill.vacancy.model.entity.VacancyApplication;
import com.bmu1093a.quill.vacancy.model.entity.enumeration.ApplicationStatus;
import com.bmu1093a.quill.vacancy.respository.VacancyApplicationRepository;
import com.bmu1093a.quill.vacancy.respository.VacancyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VacancyApplicationServiceTest {

    @Mock
    private VacancyApplicationRepository vacancyApplicationRepository;

    @Mock
    private VacancyRepository vacancyRepository;

    @Mock
    private VacancyApplicationMapper vacancyApplicationMapper;

    @Mock
    private UserLookupService userLookupService;

    @InjectMocks
    private VacancyApplicationService service;

    private User user() {
        return User.builder().id(1L).build();
    }

    private Vacancy vacancy(boolean active) {
        Vacancy v = new Vacancy();
        v.setIsActive(active);
        return v;
    }

    // ---------------- APPLY SUCCESS ----------------

    @Test
    void applyToVacancy_success() {
        User user = user();
        Vacancy vacancy = vacancy(true);

        when(userLookupService.getCurrentUser()).thenReturn(user);
        when(vacancyRepository.findById(1L)).thenReturn(Optional.of(vacancy));
        when(vacancyApplicationRepository.existsByUser_IdAndVacancy_IdAndStatusIn(
                eq(1L), eq(1L), anyList())).thenReturn(false);
        when(vacancyApplicationRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));
        when(vacancyApplicationMapper.toVacancyApplicationResponseDto(any()))
                .thenReturn(new VacancyApplicationResponseDto());

        VacancyApplicationResponseDto result = service.applyToVacancy(1L);

        assertNotNull(result);
        verify(vacancyApplicationRepository).save(any());
    }

    // ---------------- VACANCY NOT FOUND ----------------

    @Test
    void applyToVacancy_shouldThrow_whenVacancyNotFound() {
        when(userLookupService.getCurrentUser()).thenReturn(user());
        when(vacancyRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.applyToVacancy(1L));
    }

    // ---------------- INACTIVE VACANCY ----------------

    @Test
    void applyToVacancy_shouldThrow_whenVacancyInactive() {
        when(userLookupService.getCurrentUser()).thenReturn(user());
        when(vacancyRepository.findById(1L)).thenReturn(Optional.of(vacancy(false)));

        assertThrows(VacancyNotActiveException.class,
                () -> service.applyToVacancy(1L));
    }

    // ---------------- ALREADY APPLIED ----------------

    @Test
    void applyToVacancy_shouldThrow_whenAlreadyApplied() {
        User user = user();
        when(userLookupService.getCurrentUser()).thenReturn(user);
        when(vacancyRepository.findById(1L)).thenReturn(Optional.of(vacancy(true)));
        when(vacancyApplicationRepository.existsByUser_IdAndVacancy_IdAndStatusIn(
                eq(1L), eq(1L), anyList())).thenReturn(true);

        assertThrows(AlreadyAppliedException.class,
                () -> service.applyToVacancy(1L));
    }

    // ---------------- GET APPLICATIONS ----------------

    @Test
    void getApplicationsByVacancyId_success() {
        when(vacancyApplicationRepository.findVacancyApplicationsByVacancyId(1L))
                .thenReturn(List.of(new VacancyApplication(), new VacancyApplication()));
        when(vacancyApplicationMapper.toVacancyApplicantDtoResponse(any()))
                .thenReturn(new VacancyApplicantResponseDto());

        List<VacancyApplicantResponseDto> result = service.getApplicationsByVacancyId(1L);

        assertEquals(2, result.size());
    }

    // ---------------- CANCEL SUCCESS ----------------

    @Test
    void cancelVacancyApplication_success() {
        User user = user();
        VacancyApplication application = VacancyApplication.builder()
                .id(100L)
                .user(user)
                .status(ApplicationStatus.PENDING)
                .build();

        when(userLookupService.getCurrentUser()).thenReturn(user);
        when(vacancyApplicationRepository.findByVacancy_IdAndUser_IdAndStatus(
                1L, 1L, ApplicationStatus.PENDING))
                .thenReturn(Optional.of(application));

        assertDoesNotThrow(() -> service.cancelVacancyApplication(1L));
        verify(vacancyApplicationRepository).save(application);
    }

    // ---------------- APPLICATION NOT FOUND ----------------

    @Test
    void cancelVacancyApplication_shouldThrow_whenNotFound() {
        when(userLookupService.getCurrentUser()).thenReturn(user());
        when(vacancyApplicationRepository.findByVacancy_IdAndUser_IdAndStatus(
                1L, 1L, ApplicationStatus.PENDING))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.cancelVacancyApplication(1L));

        verify(vacancyApplicationRepository, never()).save(any());
    }
}