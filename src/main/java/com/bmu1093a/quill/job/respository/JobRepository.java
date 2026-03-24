package com.bmu1093a.quill.job.respository;

import com.bmu1093a.quill.job.model.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Long> {
}
