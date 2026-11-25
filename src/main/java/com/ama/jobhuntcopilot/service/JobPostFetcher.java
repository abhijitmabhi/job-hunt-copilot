package com.ama.jobhuntcopilot.service;

import org.jsoup.Jsoup;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@Service
public class JobPostFetcher {

    private static final int TIMEOUT_MILLIS = 10_000;
    private static final int MAX_TEXT_LENGTH = 50_000;

    public String fetchText(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only http and https URLs are supported");
        }
        try {
            String text = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (compatible; job-hunt-copilot)")
                    .timeout(TIMEOUT_MILLIS)
                    .get()
                    .body()
                    .text();
            if (text.isBlank()) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT, "No text found at " + url);
            }
            return text.length() > MAX_TEXT_LENGTH ? text.substring(0, MAX_TEXT_LENGTH) : text;
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Could not fetch job posting from " + url, e);
        }
    }
}
