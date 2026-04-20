package com.bmu1093a.quill.vacancy.respository;

import com.bmu1093a.quill.vacancy.model.entity.Vacancy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VacancyRepository extends JpaRepository<Vacancy, Long> {
    List<Vacancy> findByStartupId(Long startupId);
}
