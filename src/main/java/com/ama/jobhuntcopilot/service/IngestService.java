package com.ama.jobhuntcopilot.service;

import com.ama.jobhuntcopilot.config.ProfileConfig;
import com.ama.jobhuntcopilot.dto.IngestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class IngestService {
    static final String TYPE_PROFILE = "profile";
    private static final Logger log = LoggerFactory.getLogger(IngestService.class);
    private final ProfileConfig profileConfig;
    private final TokenTextSplitter splitter = TokenTextSplitter.builder()
            .withChunkSize(400)
            .build();

    public IngestService(ProfileConfig profileConfig) {
        this.profileConfig = profileConfig;
    }

    private static Document withTitleInText(Document doc) {
        Object title = doc.getMetadata().get("title");
        if (title == null) {
            return doc;
        }
        return new Document(title + "\n" + doc.getText(), doc.getMetadata());
    }

    private static String preview(String text) {
        return text.substring(0, Math.min(60, text.length())).replace('\n', ' ');
    }

    public IngestResult ingest() {
        try {
            List<Path> files = listProfileFiles();
            List<Document> documents = new ArrayList<>();
            for (Path file : files) {
                List<Document> docs = read(file);
                log.info("ingest: {} -> {} documents", file.getFileName(), docs.size());
                documents.addAll(docs);
            }

            List<Document> useful = documents.stream()
                    .filter(d -> d.getText() != null && !d.getText().isBlank())
                    .map(IngestService::withTitleInText)
                    .toList();

            List<Document> chunks = splitter.apply(useful);

            log.info("ingest: {} documents -> {} useful -> {} chunks", documents.size(), useful.size(), chunks.size());
            chunks.forEach(c -> log.debug("  chunk meta={} text='{}'", c.getMetadata(), preview(c.getText())));

            return new IngestResult(files.size(), 0, chunks.size());
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read profile files", e);
        }
    }

    private List<Path> listProfileFiles() throws IOException {
        Path dir = profileConfig.dir().getFile().toPath();
        try (Stream<Path> files = Files.list(dir)) {
            return files
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().toLowerCase().endsWith(".md"))
                    .sorted()
                    .toList();
        }
    }

    private List<Document> read(Path file) {
        String source = file.getFileName().toString();

        MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                .withHorizontalRuleCreateDocument(true)
                .withIncludeCodeBlock(true)
                .withIncludeBlockquote(true)
                .withAdditionalMetadata("type", TYPE_PROFILE)
                .withAdditionalMetadata("source", source)
                .build();

        return new MarkdownDocumentReader(new FileSystemResource(file), config).get();
    }
}
