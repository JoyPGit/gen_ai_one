package com.example.ai_proj_one.chat.openai.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record SummarizationResponse(
        List<String> actionItems,
        List<String>decisions,
        String errorMsg
) {
}
