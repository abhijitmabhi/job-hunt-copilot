package com.ama.jobhuntcopilot.service;

import com.ama.jobhuntcopilot.config.ProfileConfig;
import com.ama.jobhuntcopilot.dto.IngestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class IngestService {
    private static final Logger log = LoggerFactory.getLogger(IngestService.class.getName());
    private final ProfileConfig profileConfig;

    public IngestService(ProfileConfig profileConfig) {
        this.profileConfig = profileConfig;
    }

    public IngestResult ingest() {

        // Extract
        try {
            List<Path> files = listProfileFiles();
            List<Document> documents = new ArrayList<>();

            for (Path file : files) {
                List<Document> docs = read(file);
                log.info("ingest: {} -> {} documents", file.getFileName(), docs.size());
                documents.addAll(docs);
            }

            documents.forEach(d -> log.info("  meta={} text='{}'",
                    d.getMetadata(),
                    d.getText().substring(0, Math.min(60, d.getText().length())).replace('\n', ' ')));

            return new IngestResult(files.size(), 0, 0);
        } catch (IOException e) {
            throw new RuntimeException("Failed to list profile files", e);

        }

        // Transform

        // Load

    }

    private List<Path> listProfileFiles() throws IOException {
        Path dir = profileConfig.dir().getFile().toPath();

        try (Stream<Path> files = Files.list(dir)) {
            return files
                    .filter(Files::isRegularFile)
                    .filter(p -> {
                        String fileName = p.getFileName().toString().toLowerCase();
                        return fileName.endsWith(".md") || fileName.endsWith(".pdf");
                    })
                    .sorted()
                    .toList();
        }
    }

    private List<Document> read(Path file) {
        Resource resource = new FileSystemResource(file);
        String fileName = file.getFileName().toString().toLowerCase();
        if (fileName.endsWith(".md")) {
            // Read markdown file
            MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                    .withHorizontalRuleCreateDocument(true)
                    .withIncludeCodeBlock(true)
                    .withIncludeBlockquote(true)
                    .build();

            return new MarkdownDocumentReader(resource, config).get();
        }
        // Read PDF file
        return new PagePdfDocumentReader(resource).get();

    }
}
