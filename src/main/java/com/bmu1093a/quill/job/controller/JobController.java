package com.bmu1093a.quill.job.controller;

import com.bmu1093a.quill.job.model.dto.request.JobRequestDto;
import com.bmu1093a.quill.job.model.dto.response.JobResponseDto;
import com.bmu1093a.quill.job.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @PostMapping("jobs")
    public JobResponseDto createJob(@RequestBody JobRequestDto jobRequestDto) {
        return jobService.createJob(jobRequestDto);
    }
}
