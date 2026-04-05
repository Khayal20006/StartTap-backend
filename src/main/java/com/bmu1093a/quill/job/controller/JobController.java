package com.bmu1093a.quill.job.controller;

import com.bmu1093a.quill.job.model.dto.request.JobRequestDto;
import com.bmu1093a.quill.job.model.dto.response.JobResponseDto;
import com.bmu1093a.quill.job.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @PostMapping("jobs")
    public JobResponseDto createJob(@RequestBody JobRequestDto jobRequestDto) {
        return jobService.createJob(jobRequestDto);
    }

    @GetMapping("jobs")
    public List<JobResponseDto> getAllJobs() {
        return jobService.getAllJobs();
    }

    @GetMapping("jobs/{id}")
    public JobResponseDto getJob(@PathVariable Long id) {
        return jobService.getJob(id);
    }
}
