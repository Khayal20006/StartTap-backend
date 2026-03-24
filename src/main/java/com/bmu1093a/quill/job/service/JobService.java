package com.bmu1093a.quill.job.service;

import com.bmu1093a.quill.auth.model.entity.User;
import com.bmu1093a.quill.job.mapper.JobMapper;
import com.bmu1093a.quill.job.model.dto.request.JobRequestDto;
import com.bmu1093a.quill.job.model.dto.response.JobResponseDto;
import com.bmu1093a.quill.job.model.entity.Job;
import com.bmu1093a.quill.job.respository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final UserLookupService userLookupService;
    private final JobMapper jobMapper;

    public JobResponseDto createJob(JobRequestDto jobRequestDto) {
        User user = userLookupService.getCurrentUser();

        Job job = Job.builder()
                .title(jobRequestDto.getTitle())
                .description(jobRequestDto.getDescription())
                .salary(jobRequestDto.getSalary())
                .employer(user)
                .build();

        jobRepository.save(job);

        return jobMapper.toJobResponseDto(job);
    }
}
