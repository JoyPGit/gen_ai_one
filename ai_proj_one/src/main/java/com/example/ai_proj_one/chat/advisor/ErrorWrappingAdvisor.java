package com.example.ai_proj_one.chat.advisor;

import com.example.ai_proj_one.chat.openai.dto.response.SummarizationResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.*;
import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.*;

import static com.example.ai_proj_one.utils.Constants.OBJECT_MAPPER;

@Slf4j
@Component
public class ErrorWrappingAdvisor implements CallAdvisor, StreamAdvisor {

    private final ObjectMapper objectMapper;

    public ErrorWrappingAdvisor(@Qualifier(OBJECT_MAPPER) ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        log.info("Request received in ErrorWrappingAdvisor wth prompt {}",
                chatClientRequest.prompt().getUserMessage().getText());

        ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(chatClientRequest);
        String advisorMsg =
                chatClientResponse.chatResponse().getResult().getOutput().getText().trim();

        if (!advisorMsg.startsWith("```json") && !advisorMsg.startsWith("{")) {
            SummarizationResponse summarizationResponse =
                    SummarizationResponse.builder()
                            .errorMsg(advisorMsg)
                            .build();
            try {

                String json = objectMapper.writeValueAsString(summarizationResponse);

                return chatClientResponse.mutate()
                        .chatResponse(ChatResponse.builder()
                                .generations(List.of(
                                        new Generation(new AssistantMessage(json))
                                ))
                                .build())
                        .context(Map.copyOf(chatClientRequest.context()))
                        .build();

            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return chatClientResponse;
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {
        return streamAdvisorChain.nextStream(chatClientRequest);
    }

    @Override
    public String getName() {
        return "ErrorWrappingAdvisor";
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE; //   Ordered.HIGHEST_PRECEDENCE;
        // return
    }
}
