package com.example.ai_proj_one.chat.dockermodelrunner.controller;

import com.example.ai_proj_one.chat.dockermodelrunner.tool.OrderStatusTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import static com.example.ai_proj_one.utils.Constants.*;


@Slf4j
@RestController
@RequestMapping("/api/docker-model-runner-memory-vector/chat")
public class DockerModelRunnerWithMemoryVectorController {

    private final ChatClient chatClient;
    private final OrderStatusTools orderStatusTools;
    private final ChatMemory chatMemory;
    private final EmbeddingModel embeddingModel;


    DockerModelRunnerWithMemoryVectorController(@Qualifier(OPENAI_CHAT_CLIENT_QUALIFIER_WITH_MEMORY_VECTOR) ChatClient chatClient, OrderStatusTools orderStatusTools, ChatMemory chatMemory,
            @Qualifier(OPENAI_EMBEDDING_MODEL) EmbeddingModel embeddingModel) {
        this.chatClient = chatClient;
        this.orderStatusTools = orderStatusTools;
        this.chatMemory = chatMemory;
        this.embeddingModel = embeddingModel;
    }


    private static final String SYSTEM_PROMPT = """
    You are Order Helper, a virtual assistant that helps users track their orders.
    
    Your responsibilities:
    - Help users check the shipping status of their orders.
    - Order IDs follow the format ORD-{number}, for example ORD-2.
    - If the user provides only a number, normalize it to ORD-{number}.
    - If the user provides an order ID, call getOrderStatus(orderId).
    - Never assume or invent order statuses. Always use the tool for status information.
    - If the user has not provided an order ID and none is available in relevant conversation context, ask for it.
    - For unrelated questions, politely explain that you can only help with order tracking.
    - Keep responses short, clear, and customer-friendly.
    - Address the customer by name when that information is available.
    - Ask whether the customer needs any further help.
    """;


    @GetMapping("/test-embedding")
    public String testEmbedding() {
        String query = "What is the status of ORD-1";
        float[] result = embeddingModel.embed(query);
        return "Embedding dimensions: " + result.length;
    }

    /**
     * ChatMemory.CONVERSATION_ID is overridden by userId
     * <p>
     * check before and after using MessageChatMemoryAdvisor
     * initially it has no entries, and 2 entries are made user_message and
     * assistant_message, which stores what the LLM replied
     *
     * @param message
     * @param userId
     * @return
     */
    @PostMapping("/order-status")
    public String orderStatus(@RequestBody String message,
            @RequestHeader(value = "user-id") String userId) {
        log.info("Chat memory for current user " + userId);
        chatMemory.get(userId).forEach(m -> log.info("{} : {}", m.getMessageType(),
                m.getMetadata()));

        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Message cannot be empty");
        }

        String userMsg = message + ". user-id: " + userId;
        return chatClient.prompt()
                .tools(orderStatusTools)
                .system(SYSTEM_PROMPT)
                .user(u -> u.text("User query: {userMsg}").param("userMsg", userMsg))
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, userId))
                .call()
                .content();
    }
}
