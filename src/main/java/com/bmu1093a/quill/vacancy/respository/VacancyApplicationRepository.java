package com.bmu1093a.quill.vacancy.respository;

import com.bmu1093a.quill.vacancy.model.entity.VacancyApplication;
import com.bmu1093a.quill.vacancy.model.entity.enumeration.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VacancyApplicationRepository extends JpaRepository<VacancyApplication, Long> {
    List<VacancyApplication> findVacancyApplicationsByVacancyId(Long vacancyId);


    boolean existsByUser_IdAndVacancy_IdAndStatusIn(
            Long userId, Long vacancyId, List<ApplicationStatus> statuses
    );

    Optional<VacancyApplication> findByVacancy_IdAndUser_IdAndStatus(
            Long vacancyId, Long userId, ApplicationStatus status
    );
}
