package com.bmu1093a.quill.job.respository;

import com.bmu1093a.quill.job.model.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    boolean existsByUserIdAndJobId(Long userId, Long jobId);

}
