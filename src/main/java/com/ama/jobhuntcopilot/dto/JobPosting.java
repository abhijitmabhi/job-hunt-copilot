package com.ama.jobhuntcopilot.dto;

import java.math.BigDecimal;

enum Seniority {
    JUNIOR,
    MID_LEVEL,
    SENIOR,
    LEAD
}

public record JobPosting(
        String title,
        String company,
        String location,
        Boolean remote,
        String language,
        String mustHaves,
        String niceToHaves,
        BigDecimal salary,
        Seniority seniority
) {
}
