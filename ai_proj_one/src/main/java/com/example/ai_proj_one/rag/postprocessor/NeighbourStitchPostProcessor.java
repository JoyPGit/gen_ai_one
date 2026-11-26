package com.example.ai_proj_one.rag.postprocessor;

import com.example.ai_proj_one.rag.config.data.RagConfigData;
import com.example.ai_proj_one.rag.contants.RagConstants;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.postretrieval.document.DocumentPostProcessor;
import org.springframework.ai.vectorstore.*;
import org.springframework.ai.vectorstore.filter.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.example.ai_proj_one.utils.Constants.PG_VECTOR_STORE_RAG;

/**
 * Adds neighbor chunks (chunk_index ± N) for each retrieved doc, by source.
 * Assumes metadata contains: source (String), chunk_index (Number/int).
 * Needs to be added to RAG advisor
 */
@Component
public class NeighbourStitchPostProcessor implements DocumentPostProcessor {

    private final VectorStore vectorStore;
    private final RagConfigData ragConfigData;


    public NeighbourStitchPostProcessor(@Qualifier(PG_VECTOR_STORE_RAG) VectorStore vectorStore,
            RagConfigData ragConfigData) {
        this.vectorStore = vectorStore;
        this.ragConfigData = ragConfigData;
    }

    /**
     * documents returned from similarity search
     * @param query
     * @param documents
     * @return
     */
    @Override
    public List<Document> process(Query query, List<Document> documents) {
        if(documents.isEmpty()) {
            return documents;
        }
        Set<Target> targetDocuments = getTargetDocuments(documents);
        // after getting target docs' source and index, get the exact docs
        List<Document> neighbours = getNeighbourDocs(targetDocuments);
        // after getting target docs merge the docs
        List<Document> merged = getMergedDocs(documents, neighbours);
        return deDupBySourceAndIndexPreserveOrder(merged);
    }


    /**
     * *******************************
     * HELPER METHODS
     * *******************************
     */

    private List<Document> getNeighbourDocs(Set<Target> targetDocuments) {
        List<Document> neighbours = new ArrayList<>();
        for(var document : targetDocuments) {
            // create a search request to search the vector store
            // then perform the similarity search
            var searchReq = getSearchRequest(document);
            var hits = vectorStore.similaritySearch(searchReq);
            if (!hits.isEmpty()) {
                neighbours.addAll(hits);
            }
        }
        return neighbours;
    }

    private SearchRequest getSearchRequest(Target targetDocument) {
        FilterExpressionBuilder builder = new FilterExpressionBuilder();
        Filter.Expression expression = builder.and(
                builder.eq(RagConstants.SOURCE, targetDocument.source()),
                builder.eq(RagConstants.CHUNK_INDEX, targetDocument.index())
        ).build();
        // query, topK and similarityThreshold are dummy
        // added because required, filterExpression is enough
        return SearchRequest.builder()
                .query("__neighbour__")
                .topK(1)
                .similarityThreshold(0.0)
                .filterExpression(expression)
                .build();
    }


    private Set<Target> getTargetDocuments(List<Document> documents) {
        Set<Target> targetSet = new LinkedHashSet<>();
        for(var document : documents) {
            String source =
                    String.valueOf(document.getMetadata().get(RagConstants.SOURCE));
            Object indexObj = document.getMetadata().get(RagConstants.CHUNK_INDEX);
            if(source == null || indexObj == null) {
                continue;
            }
            int index = (indexObj instanceof Number) ? (int) indexObj :
                    Integer.parseInt(indexObj.toString());

            // check radius, and choose neighbors to fetch
            for(int delta = 1; delta <= ragConfigData.getRadius(); delta++) {
                targetSet.add(new Target(source, index+delta));
                targetSet.add(new Target(source, index-delta));
            }
        }
        return targetSet;
    }

    private List<Document> getMergedDocs(List<Document> documents, List<Document> neighbours) {
        List<Document> merged = new ArrayList<>(documents.size() + neighbours.size());
        merged.addAll(documents);
        merged.addAll(neighbours);
        return merged;
    }

    private List<Document> deDupBySourceAndIndexPreserveOrder(List<Document> merged) {
        List<Document> finalDedupList = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (var doc : merged) {
            String source = doc.getMetadata().get(RagConstants.SOURCE).toString();
            String chunkIndex = doc.getMetadata().get(RagConstants.CHUNK_INDEX).toString();

            String key = source + "#" + chunkIndex;
            if (seen.contains(key)) continue;
            finalDedupList.add(doc);
            seen.add(key);
        }
        return finalDedupList;
    }
}
