package com.bmu1093a.quill.job.service;

import com.bmu1093a.quill.auth.model.entity.User;
import com.bmu1093a.quill.job.mapper.JobMapper;
import com.bmu1093a.quill.job.mapper.UserMapper;
import com.bmu1093a.quill.job.model.dto.request.JobRequestDto;
import com.bmu1093a.quill.job.model.dto.response.EmployeeResponseDto;
import com.bmu1093a.quill.job.model.dto.response.JobResponseDto;
import com.bmu1093a.quill.job.model.entity.Job;
import com.bmu1093a.quill.job.respository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final UserLookupService userLookupService;
    private final JobMapper jobMapper;
    private final UserMapper userMapper;


    public JobResponseDto createJob(JobRequestDto jobRequestDto) {
        User user = userLookupService.getCurrentUser();

        EmployeeResponseDto employeeResponseDto = userMapper.toEmployeeResponseDto(user);

        Job job = Job.builder()
                .title(jobRequestDto.getTitle())
                .description(jobRequestDto.getDescription())
                .salary(jobRequestDto.getSalary())
                .employer(user)
                .build();

        jobRepository.save(job);

        JobResponseDto jobResponseDto = jobMapper.toJobResponseDto(job);
        jobResponseDto.setEmployer(employeeResponseDto);

        return jobResponseDto;
    }

    public List<JobResponseDto> getAllJobs() {
        List<Job> jobs = jobRepository.findAll();
        return jobs.stream().map(jobMapper::toJobResponseDto).toList();
    }

    public JobResponseDto getJob(Long id) {
        Optional<Job> optional = jobRepository.findById(id);
        Job job = optional.orElseThrow(() -> new RuntimeException("Job not found"));

        EmployeeResponseDto employeeResponseDto = userMapper.toEmployeeResponseDto(job.getEmployer());

        JobResponseDto jobResponseDto = jobMapper.toJobResponseDto(job);

        jobResponseDto.setEmployer(employeeResponseDto);

        return jobResponseDto;

    }
}
