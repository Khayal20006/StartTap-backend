package com.bmu1093a.quill.job.service;

import com.bmu1093a.quill.auth.model.entity.User;
import com.bmu1093a.quill.auth.repository.UserRepository;
import com.bmu1093a.quill.job.model.dto.request.JobApplicationRequestDto;
import com.bmu1093a.quill.job.model.dto.response.JobApplicationResponseDto;
import com.bmu1093a.quill.job.model.entity.Job;
import com.bmu1093a.quill.job.model.entity.JobApplication;
import com.bmu1093a.quill.job.respository.JobApplicationRepository;
import com.bmu1093a.quill.job.respository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final UserLookupService userLookupService;

    public JobApplicationResponseDto applyToJob(JobApplicationRequestDto jobApplicationRequestDto) {
        User currentUser = userLookupService.getCurrentUser();

        Long jobId = jobApplicationRequestDto.getJobId();
        Long userId = currentUser.getId();

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!job.getIsActive()) {
            throw new IllegalStateException("Job is not active");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean alreadyApplied = jobApplicationRepository
                .existsByUserIdAndJobId(userId, jobId);

        if (alreadyApplied) {
            throw new IllegalStateException("Already applied");
        }

        JobApplication jobApplication = JobApplication.builder()
                .user(user)
                .job(job)
                .build();

        jobApplicationRepository.save(jobApplication);

        return mapToJobApplicationResponseDto(jobApplication);

    }

    private JobApplicationResponseDto mapToJobApplicationResponseDto(JobApplication jobApplication) {
        return JobApplicationResponseDto.builder()
                .id(jobApplication.getId())
                .jobId(jobApplication.getJob().getId())
                .jobTitle(jobApplication.getJob().getTitle())
                .status(jobApplication.getStatus())
                .appliedAt(jobApplication.getAppliedAt())
                .build();
    }
}
