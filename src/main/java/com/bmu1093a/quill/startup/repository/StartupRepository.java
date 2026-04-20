package com.bmu1093a.quill.startup.repository;

import com.bmu1093a.quill.startup.model.entity.Startup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StartupRepository extends JpaRepository<Startup, Long> {
}
