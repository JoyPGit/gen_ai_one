package com.example.ai_proj_one.config;

import com.example.ai_proj_one.chat.advisor.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.*;
import org.springframework.ai.chat.memory.*;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;

import java.util.List;

import static com.example.ai_proj_one.utils.Constants.*;

/**
 * model used to create client
 */
@Configuration
public class AIProviderConfig {

    /**
     * the first advisor in the chain will be called first
     * check precedence, lower the order, higher the priority
     *
     * adding safeGuardAdvisor at the advisor blocks the request, response logs
     * @param openAiChatModel
     * @param simpleLoggerAdvisor
     * @return
     */
    @Bean(OPENAI_CHAT_CLIENT_QUALIFIER)
    ChatClient openAIChatClient(OpenAiChatModel openAiChatModel,
                                SimpleLoggerAdvisor simpleLoggerAdvisor,
                                @Qualifier(SAFEGUARD_ADVISOR) SafeGuardAdvisor safeGuardAdvisor,
                                ErrorWrappingAdvisor errorWrappingAdvisor,
                                SystemPromptAdvisor systemPromptAdvisor) {
        return ChatClient.builder(openAiChatModel)
                .defaultAdvisors(safeGuardAdvisor, systemPromptAdvisor,
                        simpleLoggerAdvisor,
                        errorWrappingAdvisor)
                .build();
    }

//    @Bean(OPENAI_CHAT_CLIENT_QUALIFIER)
//    ChatClient openAIChatClient(OpenAiChatModel openAiChatModel) {
//        return ChatClient.builder(openAiChatModel)
//                .defaultAdvisors()
//                .build();
//    }


    /**
     */
    @Bean(OPENAI_CHAT_CLIENT_QUALIFIER_WITH_MEMORY)
    ChatClient openAIChatClientWithMemory(OpenAiChatModel openAiChatModel,
                                ChatMemory chatMemory) {
        return ChatClient.builder(openAiChatModel)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    @Bean(VERTEXAI_CHAT_CLIENT_QUALIFIER)
    ChatClient vertexAIChatClient(OpenAiChatModel openAiChatModel) {
        return ChatClient.builder(openAiChatModel).build();
    }

    @Bean(HUGGINGFACEAI_CHAT_CLIENT_QUALIFIER)
    ChatClient huggingFaceAIChatClient(OpenAiChatModel openAiChatModel) {
        return ChatClient.builder(openAiChatModel).build();
    }

    @Bean(OLLAMAAI_CHAT_CLIENT_QUALIFIER)
    ChatClient ollamaAIChatClient(OpenAiChatModel ollamaChatModel) {
        return ChatClient.builder(ollamaChatModel).build();
    }

    /**
     * has order value = 0
     * has log level DEBUG, check in application.yml
     * chat.client.advisor.SimpleLoggerAdvisor: DEBUG
     * keep it OFF for prod
     * @return
     */
    @Bean
    SimpleLoggerAdvisor simpleLoggerAdvisor() {
        return new SimpleLoggerAdvisor();
    }

    /**
     * for sensitive content including these keywords, reject
     * @return
     */
    @Bean(SAFEGUARD_ADVISOR)
    SafeGuardAdvisor safeGuardAdvisor() {
        return new SafeGuardAdvisor(List.of(
                "password", "ssn", "credit card", "confidential"));
    }

    @Bean(OBJECT_MAPPER)
    ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    /**
     * stores in a concurrent hash map
     * can use to set max messages in memory
     * @param chatMemoryRepository
     * @return
     */
    @Bean(CHAT_MEMORY_NON_JDBC)
    ChatMemory chatMemory(ChatMemoryRepository chatMemoryRepository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(20)
                .build();
    }
}
