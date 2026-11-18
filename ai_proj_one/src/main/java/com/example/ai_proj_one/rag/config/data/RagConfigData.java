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
    private int similarityThreshold;
    private ChunkProperties chunk;

    @Data
    public static class ChunkProperties {
        private int size;
        private int minChunkSize;
        private int minChunkToEmbed;
        private int maxNumChunks;
        private boolean keepSeparator;
    }
}
