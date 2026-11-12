package com.example.ai_proj_one.chat.dockermodelrunner.controller;

import com.example.ai_proj_one.chat.dockermodelrunner.tool.OrderStatusTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.*;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import static com.example.ai_proj_one.chat.util.LogUtil.logTokenInfo;
import static com.example.ai_proj_one.utils.Constants.*;

@Slf4j
@RestController
@RequestMapping("/api/docker-model-runner-memory/chat")
public class DockerModelRunnerWithMemoryController {

    private final ChatClient chatClient;
    private final OrderStatusTools orderStatusTools;
    private final ChatMemory chatMemory;

    public DockerModelRunnerWithMemoryController(@Qualifier(OPENAI_CHAT_CLIENT_QUALIFIER_WITH_MEMORY) ChatClient chatClient, OrderStatusTools orderStatusTools, ChatMemory chatMemory) {
        this.chatClient = chatClient;
        this.orderStatusTools = orderStatusTools;
        this.chatMemory = chatMemory;
    }


    private final static String SYSTEM_PROMPT = " You are Order Helper, a virtual assistant that helps users track their orders. " +
            "Your tasks" +
            "- assist the user with checking, the shipping status of their " +
            "orders. " +
            "- if the user provides an order ID or if you can find the order via it " +
            "from chat memory, use the available tool, getOrderStatus(orderId)." +
            "- If the user asks about something unrelated to orders, that is weather, " +
            "jokes, general chit-chat, politely refuse and remind them you can only " +
            "help with order tracking" +
            "Guidelines: " +
            "- Do not support statuses; only use the tool for that information. " +
            "- If you can find the order ID from chat memory, use it to answer directly; otherwise politely ask the user to provide it. " +
            "- Keep responses short, clear, and customer-friendly. Respond by naming " +
            "the customer and asking for further help.";


    @PostMapping("/general")
    public ChatClientResponse generalChat(@RequestBody String message) {
        ChatClientResponse chatClientResponse = chatClient.prompt()
                .user(message)
                .call()
                .chatClientResponse();
        logTokenInfo(message, chatClientResponse);
        return chatClientResponse;
    }

    /**
     * ChatMemory.CONVERSATION_ID is overridden by userId
     *
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

        String userMsg = message + ". user-id: "+ userId;
        return chatClient.prompt()
                .tools(orderStatusTools)
                .system(SYSTEM_PROMPT)
                .user(u -> u.text("User query: {userMsg}").param("userMsg", userMsg))
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, userId))
                .call()
                .content();
    }


}
