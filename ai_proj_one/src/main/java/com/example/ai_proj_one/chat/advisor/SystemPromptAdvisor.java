package com.example.ai_proj_one.chat.advisor;

import org.springframework.ai.chat.client.*;
import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.*;

@Component
public class SystemPromptAdvisor implements CallAdvisor, StreamAdvisor {

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        chatClientRequest = updateSystemMessage(chatClientRequest);
        return callAdvisorChain.nextCall(chatClientRequest);
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {
        chatClientRequest = updateSystemMessage(chatClientRequest);
        return streamAdvisorChain.nextStream(chatClientRequest);
    }

    @Override
    public String getName() {
        return "SystemPromptAdvisor";
    }

    @Override
    public int getOrder() {
        return 0;
    }

    private ChatClientRequest updateSystemMessage(ChatClientRequest chatClientRequest) {
        List<Message> userMessages =
                new ArrayList<>(chatClientRequest.prompt().getUserMessages());
        SystemMessage existingSysMsg = chatClientRequest.prompt().getSystemMessage();
        userMessages.add(new SystemMessage("""
                You are a summarizer for a given text or context. 
                Do not answer anything other than the summarization. 
                If the question is not about summarization, respond with 
                I can only help with summarization tasks, never provide any other information.
                """ + existingSysMsg));
        return chatClientRequest.mutate()
                .prompt(Prompt.builder()
                        .messages(userMessages).build())
                .build();
    }
}
