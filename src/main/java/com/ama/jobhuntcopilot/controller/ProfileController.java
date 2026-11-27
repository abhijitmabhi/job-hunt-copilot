package com.ama.jobhuntcopilot.controller;

import com.ama.jobhuntcopilot.dto.IngestResult;
import com.ama.jobhuntcopilot.service.IngestService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    private final IngestService ingestService;

    public ProfileController(IngestService ingestService) {
        this.ingestService = ingestService;
    }

    @PostMapping("/ingest")
    public IngestResult ingest() {
        return ingestService.ingest();
    }
}
