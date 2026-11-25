package com.ama.jobhuntcopilot.service;

import com.ama.jobhuntcopilot.dto.JobPosting;
import com.ama.jobhuntcopilot.dto.ParseJobRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ResponseEntity;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class JobService {
    private static final Logger log = LoggerFactory.getLogger(JobService.class);
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

        ResponseEntity<ChatResponse, JobPosting> result = chatClient
                .prompt()
                .system(parseJobSystemPrompt)
                .user(u -> u.text(parseJobPrompt)
                        .param("jobPost", jobPost))
                .call()
                .responseEntity(JobPosting.class);

        assert result.response() != null;
        Usage usage = result.response().getMetadata().getUsage();

        log.info("parse-job: model={}, promptTokens={}, completionTokens={}, totalTokens={}", result.response().getMetadata().getModel(), usage.getPromptTokens(), usage.getCompletionTokens(), usage.getTotalTokens());

        return result.entity();
    }

}
