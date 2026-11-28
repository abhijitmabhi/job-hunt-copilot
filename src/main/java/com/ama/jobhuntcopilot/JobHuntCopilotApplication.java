package com.ama.jobhuntcopilot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class JobHuntCopilotApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobHuntCopilotApplication.class, args);
    }

}
