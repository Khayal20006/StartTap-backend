package com.bmu1093a.quill.vacancy.respository;

import com.bmu1093a.quill.vacancy.model.entity.VacancyApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VacancyApplicationRepository extends JpaRepository<VacancyApplication, Long> {
    boolean existsByUserIdAndVacancyId(Long userId, Long vacancyId);

    List<VacancyApplication> findVacancyApplicationsByVacancyId(Long vacancyId);

}
