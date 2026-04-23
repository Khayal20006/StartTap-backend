package com.bmu1093a.quill.startup.repository;

import com.bmu1093a.quill.auth.model.entity.User;
import com.bmu1093a.quill.startup.model.entity.Startup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StartupRepository extends JpaRepository<Startup, Long> {
    List<Startup> findByOwner(User owner);
}
