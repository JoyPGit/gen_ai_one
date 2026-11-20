package com.example.ai_proj_one;

import com.example.ai_proj_one.rag.service.RagIngestionService;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AiProjOneApplication implements CommandLineRunner {

    private final RagIngestionService ragIngestionService;

    public AiProjOneApplication(RagIngestionService ragIngestionService) {
        this.ragIngestionService = ragIngestionService;
    }

    public static void main(String[] args) {
		SpringApplication.run(AiProjOneApplication.class, args);
	}

    /**
     * to update the vector store dynamically from terminal
     * if any updates to pdf files
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
        ragIngestionService.initialisePgVectorStore();
    }
}
