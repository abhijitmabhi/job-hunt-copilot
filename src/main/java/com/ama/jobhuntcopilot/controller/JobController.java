package com.ama.jobhuntcopilot.controller;

import com.ama.jobhuntcopilot.dto.JobPosting;
import com.ama.jobhuntcopilot.dto.ParseJobRequest;
import jakarta.validation.Valid;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/jobs")
public class JobController {
    private final ChatClient chatClient;

    public JobController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @PostMapping("/parse")
    public JobPosting parse(@Valid @RequestBody ParseJobRequest jobPostingText) {
        return chatClient
                .prompt()
                .system("""
                        Work as job agent
                        """)
                .user(String.valueOf(jobPostingText))
                .call()
                .entity(JobPosting.class);
    }
}
