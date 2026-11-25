package com.ama.jobhuntcopilot.controller;

import com.ama.jobhuntcopilot.dto.JobPosting;
import com.ama.jobhuntcopilot.dto.ParseJobRequest;
import com.ama.jobhuntcopilot.service.JobPostFetcher;
import jakarta.validation.Valid;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/jobs")
public class JobController {
    private final ChatClient chatClient;
    private final JobPostFetcher jobPostFetcher;

    @Value("classpath:prompts/parse-job-system.st")
    private Resource parseJobSystemPrompt;
    @Value("classpath:prompts/parse-job.st")
    private Resource parseJobPrompt;

    public JobController(ChatClient.Builder chatClientBuilder, JobPostFetcher jobPostFetcher) {
        this.chatClient = chatClientBuilder.build();
        this.jobPostFetcher = jobPostFetcher;
    }

    @PostMapping("/parse")
    public JobPosting parse(@Valid @RequestBody ParseJobRequest jobPostingText) {
        String jobPost = jobPostingText.text() != null && !jobPostingText.text().isBlank()
                ? jobPostingText.text()
                : jobPostFetcher.fetchText(jobPostingText.url());

        return chatClient
                .prompt()
                .system(parseJobSystemPrompt)
                .user(u -> u.text(parseJobPrompt)
                        .param("jobPost", jobPost))
                .call()
                .entity(JobPosting.class);
    }
}
