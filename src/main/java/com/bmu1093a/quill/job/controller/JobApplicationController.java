package com.bmu1093a.quill.job.controller;

import com.bmu1093a.quill.job.model.dto.request.JobApplicationRequestDto;
import com.bmu1093a.quill.job.model.dto.response.JobApplicationResponseDto;
import com.bmu1093a.quill.job.service.JobApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/jobs")
@RequiredArgsConstructor
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    @PostMapping("apply")
    public ResponseEntity<JobApplicationResponseDto> applyToJob(@RequestBody JobApplicationRequestDto jobApplicationRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(jobApplicationService.applyToJob(jobApplicationRequestDto));
    }
}
