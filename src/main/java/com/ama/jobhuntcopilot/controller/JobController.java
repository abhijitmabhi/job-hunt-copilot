package com.ama.jobhuntcopilot.controller;

import com.ama.jobhuntcopilot.dto.JobPosting;
import com.ama.jobhuntcopilot.dto.ParseJobRequest;
import com.ama.jobhuntcopilot.service.JobService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/jobs")
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/parse")
    public ResponseEntity<JobPosting> parse(@Valid @RequestBody ParseJobRequest jobPostingText) {
        return ResponseEntity.ok(jobService.parseJobPosting(jobPostingText));
    }
}
