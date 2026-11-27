package com.ama.jobhuntcopilot.dto;

public record IngestResult(
        int files,
        int skipped,
        int chunks
) {
}
