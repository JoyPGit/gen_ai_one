package com.example.ai_proj_one.rag.service;

import com.example.ai_proj_one.rag.config.data.*;
import com.example.ai_proj_one.rag.contants.RagConstants;
import com.example.ai_proj_one.rag.exception.RagException;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.*;
import org.springframework.core.io.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.*;

import static com.example.ai_proj_one.utils.Constants.PG_VECTOR_STORE_RAG;

/**
 * Reads pdf files from path mentioned in application.yml
 * and stores in vector_store
 */
@Slf4j
@Service
public class RagIngestionService {

    private final VectorStore vectorStore;
    private final JdbcTemplate jdbcTemplate;
    private final RagConfigData ragConfigData;
    private final PdfDocumentReaderConfig pdfDocumentReaderConfig;
    private final TokenTextSplitter tokenTextSplitter;
    private final String ragVectorStoreTableName;

    public RagIngestionService(@Qualifier(PG_VECTOR_STORE_RAG) VectorStore vectorStore,
            JdbcTemplate jdbcTemplate,
            RagConfigData ragConfigData,
            TokenTextSplitter tokenTextSplitter, PgVectorStoreConfigData pgVectorStoreConfigData) {
        this.vectorStore = vectorStore;
        this.jdbcTemplate = jdbcTemplate;
        this.ragConfigData = ragConfigData;
        // simple pdf reading config
        RagConfigData.PdfProperties pdfProperties = ragConfigData.getPdf();
        this.pdfDocumentReaderConfig = PdfDocumentReaderConfig.builder()
                .withPagesPerDocument(pdfProperties.getPagesPerDocument())
                .withPageExtractedTextFormatter(ExtractedTextFormatter
                        .builder()
                        .withLeftAlignment(pdfProperties.isLeftAlignment())
                        .withNumberOfTopTextLinesToDelete(pdfProperties.getNumberOfTextLinesToDelete())
                        .withNumberOfBottomTextLinesToDelete(pdfProperties.getNumberOfBottomTextLinesToDelete())
                        .build())
                .build();
        this.tokenTextSplitter = tokenTextSplitter;
        this.ragVectorStoreTableName = pgVectorStoreConfigData.getTableNameForRag();
    }

    public void initialisePgVectorStore() throws RagException {
        if (skipIngest()) {
            return;
        }
        var pdfResources = getResources();
        if(pdfResources == null) {
            return;
        }

        ingestDocumentChunksToVectorStore(pdfResources);
    }

    private boolean skipIngest() {
        if(ragConfigData.isForceRebuild()) {
            log.info("force-rebuild = true -> truncating {}", ragVectorStoreTableName);
            this.jdbcTemplate.update("TRUNCATE TABLE " + ragVectorStoreTableName);
        } else {
            Integer count =
                    this.jdbcTemplate.queryForObject("SELECT count(*) from " + ragVectorStoreTableName, Integer.class);
            if (count != null && count > 0) {
                log.info("Vector store already populated with {} rows. Skipping ingest," +
                                " set ddp.rag.force-rebuild=true to rebuild",
                        count);
                return true;
            }
        }
        return false;
    }


    // why resource?
    private Resource[] getResources() {
        var resolver = new PathMatchingResourcePatternResolver();
        String pdfPath = ragConfigData.getPdf().getPath();
        try {
            Resource[] resources = resolver.getResources(pdfPath);
            if(resources.length == 0) {
                log.warn("No pdf found at path {}", pdfPath);
            }
            return resources;
        } catch (IOException e) {
            throw new RagException("Could not get pdf resources");
        }
    }

    /**
     *
     * get documents from pdf resources
     * add metadata with chunk index for re-ranking
     */
    private void ingestDocumentChunksToVectorStore(Resource[] resources) {
        var documents = getDocuments(resources);
        var chunks = getChunks(documents);
        addChunkIndex(chunks);
        vectorStore.add(chunks);
    }



    private List<Document> getDocuments(Resource[] resources) {
        List<Document> documents = new ArrayList<>();
        for(Resource pdfResource : resources) {
            List<Document> parts = getDocumentParts(pdfResource);
            addMetadata(pdfResource , parts);
            documents.addAll(parts);
        }
        return documents;
    }


    private List<Document> getDocumentParts(Resource pdfResource) {
        List<Document> parts;
        if(RagConstants.PARAGRAPH_MODE.equalsIgnoreCase(ragConfigData.getPdf().getMode())) {
            parts = new ParagraphPdfDocumentReader(pdfResource, pdfDocumentReaderConfig).read();
        } else {
            parts = new PagePdfDocumentReader(pdfResource, pdfDocumentReaderConfig).read();
        }
        return parts;
    }


    private void addMetadata(Resource pdfResource, List<Document> parts) {
        for(var part : parts) {
            part.getMetadata().putIfAbsent(RagConstants.SOURCE,
                    pdfResource.getFilename());
            // file name w/o extension
            part.getMetadata().putIfAbsent(RagConstants.DOC_TYPE,
                    Objects.requireNonNull(pdfResource.getFilename()).substring(0, pdfResource.getFilename().indexOf(".")));
            part.getMetadata().putIfAbsent(RagConstants.UPDATED_AT,
                    ZonedDateTime.now().toLocalDate().toString());
        }
    }

    // chunks via token text splitter of open ai
    private List<Document> getChunks(List<Document> documents) {
        return tokenTextSplitter.apply(documents);
    }


    // to help with neighbour stitching
    private void addChunkIndex(List<Document> chunks) {
        Map<String, Integer> counters = new HashMap<>();
        for(Document chunk : chunks) {
            var source =
                    String.valueOf(chunk.getMetadata().getOrDefault(RagConstants.SOURCE, RagConstants.UNKNOWN));
            var index = counters.merge(source, 1, Integer::sum) - 1;
            chunk.getMetadata().put(RagConstants.CHUNK_INDEX, index);
        }
    }

}
