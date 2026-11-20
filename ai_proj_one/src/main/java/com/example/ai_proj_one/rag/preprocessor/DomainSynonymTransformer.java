package com.example.ai_proj_one.rag.preprocessor;

import com.example.ai_proj_one.rag.config.data.RagConfigData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;

/**
 * InternalQAController -> RetrievalAugmentationAdvisor
 * inject here
 */
@Slf4j
@Component
public class DomainSynonymTransformer implements QueryTransformer {

    private final Map<Pattern, String> replacements;

    /**
     * '//b' -> word boundary, replacing words that match exactly
     * case-insensitive
     */
    public DomainSynonymTransformer(RagConfigData ragConfigData) {
        Map<String, String> synonymMap = ragConfigData.getSynonyms();
        this.replacements = new LinkedHashMap<>(); //more speciifc patterns to run first
        if (synonymMap != null) {
            synonymMap.forEach((k, v) ->
                    this.replacements.put(Pattern.compile("\\b" + Pattern.quote(k) + "\\b",
                            Pattern.CASE_INSENSITIVE), v));
        }
    }

    @Override
    public Query transform(Query query) {
        String queryText = query.text();
        if (queryText.isBlank()) {
            return query;
        }
        String cleanedText = getCleanedText(queryText);
        cleanedText = applyDomainSynonyms(cleanedText);
        log.info("Transformed query: {}", cleanedText);
        return Query.builder().text(cleanedText).build();
    }


    /**
     * remove please, can you etc.
     * remove empty spaces
     * remove question mark along with trailing empty spaces
     */
    private String getCleanedText(String queryText) {
        return queryText.trim()
                .replaceAll("(?i)^(please|can you|could you)\\s+", "") // polite prefixed
                .replaceAll("\\s+\\?$", ""); // trailing question mark
    }

    // replace with synonyms
    private String applyDomainSynonyms(String cleanedText) {
        for (var entry : replacements.entrySet()) {
            cleanedText = entry.getKey().matcher(cleanedText).replaceAll(entry.getValue());
        }
        return cleanedText;
    }

}
