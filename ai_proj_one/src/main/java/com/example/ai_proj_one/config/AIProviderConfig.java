package com.example.ai_proj_one.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.*;

import static com.example.ai_proj_one.utils.Constants.*;

/**
 * model used to create client
 */
@Configuration
public class AIProviderConfig {

    @Bean(OPENAI_CHAT_CLIENT_QUALIFIER)
    ChatClient openAIChatClient(OpenAiChatModel openAiChatModel) {
        return ChatClient.builder(openAiChatModel).build();
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
}
