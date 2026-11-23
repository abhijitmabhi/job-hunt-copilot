package com.ama.jobhuntcopilot;

import org.springframework.boot.SpringApplication;

public class TestJobHuntCopilotApplication {

    public static void main(String[] args) {
        SpringApplication.from(JobHuntCopilotApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
