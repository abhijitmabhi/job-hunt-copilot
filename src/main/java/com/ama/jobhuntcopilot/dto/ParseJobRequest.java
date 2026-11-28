package com.ama.jobhuntcopilot.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record ParseJobRequest(
        @Size(max = 50_000)
        String text,
        @URL
        String url
) {

    @AssertTrue(message = "Either text or url must be provided")
    boolean isTextOrUrl() {
        return text != null && !text.isBlank() || url != null && !url.isBlank();
    }
}
