package com.example.ai_proj_one.aiagent.agent.data;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 1 configuration and configuration properties
 * 2 prefix
 * 3 static class
 * 4 private static class variable
 * 5 diagramTool should match the key in application.yml
 *   agent:
 *     diagram-tool:
 *       temperature: 0.2 # low creativity for images
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "app.agent")
public class AiAgentConfigData {

    private String uploadDir;
    private DiagramToolProperties diagramTool;
    private PostureToolProperties postureTool;
    private RagToolProperties ragTool;

    @Data
    public static class DiagramToolProperties {
        private Double temperature;
    }

    @Data
    public static class PostureToolProperties {
        private String url;
        private String env;
    }

    @Data
    public static class RagToolProperties {
        private Double similarityThreshold;
        private Integer minTopK;
        private Integer maxTopK;
        private Integer defaultTopK;
    }
}
