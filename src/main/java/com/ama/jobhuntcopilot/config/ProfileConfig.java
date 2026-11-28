package com.ama.jobhuntcopilot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

@ConfigurationProperties("copilot.profile")
public record ProfileConfig(
    Resource dir
) {

}
