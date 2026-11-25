package com.ama.jobhuntcopilot.service;

import com.ama.jobhuntcopilot.dto.JobPosting;
import com.ama.jobhuntcopilot.dto.ParseJobRequest;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class JobService {
    private final ChatClient chatClient;
    private final JobPostFetcher jobPostFetcher;

    @Value("classpath:prompts/parse-job-system.st")
    private Resource parseJobSystemPrompt;
    @Value("classpath:prompts/parse-job.st")
    private Resource parseJobPrompt;

    public JobService(ChatClient.Builder chatClientBuilder, JobPostFetcher jobPostFetcher) {
        this.chatClient = chatClientBuilder.build();
        this.jobPostFetcher = jobPostFetcher;
    }

    public JobPosting parseJobPosting(ParseJobRequest jobPostingText) {
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
