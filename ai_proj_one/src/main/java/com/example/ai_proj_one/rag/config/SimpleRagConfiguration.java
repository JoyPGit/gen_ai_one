package com.example.ai_proj_one.rag.config;


import com.example.ai_proj_one.rag.config.data.RagConfigData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.*;
import java.util.*;

import static com.example.ai_proj_one.utils.Constants.*;

/**
 * ingestion pipeline
 * all 7 steps
 *      - read pdf files
 * 		- enrich metadata
 * 		- get chunks
 * 		- add to vector store
 * 		- get embeddings
 * 			- `openAI embeddings`
 * 		- store result in concurrent hashmap
 * 		- save to disc as json
 */
@Slf4j
@Configuration
public class SimpleRagConfiguration {

    private static final String FILES_PATH = "classpath:rag/mini-employee-handbook" +
            "/files/*.md";
    private static final String VECTOR_STORE_PATH = "src/main/resources/rag/mini" +
            "-employee-handbook/vector-store/vectorstore.json";


    @Bean
    public TokenTextSplitter tokenTextSplitter(RagConfigData ragConfigData) {
        return TokenTextSplitter.builder()
                .withChunkSize(ragConfigData.getChunk().getSize())
                .withMinChunkSizeChars(ragConfigData.getChunk().getMinChunkSize())
                .withMinChunkLengthToEmbed(ragConfigData.getChunk().getMinChunkToEmbed())
                .withMaxNumChunks(ragConfigData.getChunk().getMaxNumChunks())
                .withKeepSeparator(ragConfigData.getChunk().isKeepSeparator())
                .build();
    }

    /**
     * returns a concurrent hash map
     * @param embeddingModel
     * @param tokenTextSplitter
     * @param ragConfigData
     * @return
     * @throws IOException
     */
    @Bean(SIMPLE_VECTOR_STORE)
    public SimpleVectorStore simpleVectorStore(
            @Qualifier(OPENAI_EMBEDDING_MODEL) EmbeddingModel embeddingModel,
            TokenTextSplitter tokenTextSplitter,
            RagConfigData ragConfigData) throws IOException {

        var simpleVectorStore = SimpleVectorStore.builder(embeddingModel).build();

        var vectorStoreFile = new File(VECTOR_STORE_PATH);
        if(vectorStoreFile.exists() && !ragConfigData.isForceRebuild()) {
            log.info("Vector store file exists, loading it from {}", VECTOR_STORE_PATH);
            simpleVectorStore.load(vectorStoreFile);
            return simpleVectorStore;
        }

        log.info("Creating new vector store file from path: {}",
                VECTOR_STORE_PATH);

        // ingest and build new vector store
        var documents = loadDocuments();
        var chunks = getChunks(documents, tokenTextSplitter);
        // doAdd in SimpleVectorStore
        // stores in concurrent hashmap
        simpleVectorStore.add(chunks);
        simpleVectorStore.save(vectorStoreFile);
        return simpleVectorStore;
    }

    /**
     *
     * @return
     * @throws IOException
     */
    private List<Document> loadDocuments() throws IOException {
        PathMatchingResourcePatternResolver resolver =
                new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources(FILES_PATH);

        // with custom metadata, semantic retrieval and structured filtering
        List<Document> documents = new ArrayList<>();
        for(Resource resource : resources) {
            TextReader reader = new TextReader(resource);
            reader.getCustomMetadata().put("category", resource.getFilename().replace(
                    ".md", ""));
            reader.getCustomMetadata().put("access_level", "public");
            reader.getCustomMetadata().put("version", "2026.01");
            documents.addAll(reader.read());
        }

        return documents;
    }

    private List<Document> getChunks(List<Document> documents,
            TokenTextSplitter tokenTextSplitter) {
        return tokenTextSplitter.apply(documents);
    }
}
