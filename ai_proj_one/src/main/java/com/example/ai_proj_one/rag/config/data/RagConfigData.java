package com.example.ai_proj_one.rag.config.data;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * to pick config keys from application.yml
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "app.rag")
public class RagConfigData {
    private boolean forceRebuild;
    private int topK;
    private Double similarityThreshold;
    private ChunkProperties chunk;
    private PdfProperties pdf; // same key name as in application.yml

    @Data
    public static class ChunkProperties {
        private int size;
        private int minChunkSize;
        private int minChunkToEmbed;
        private int maxNumChunks;
        private boolean keepSeparator;
    }

    @Data
    public static class PdfProperties {
        private String mode;
        private String path;
        private int pagesPerDocument;
        private boolean leftAlignment;
        private int numberOfTextLinesToDelete;
        private int numberOfBottomTextLinesToDelete;
    }
}
