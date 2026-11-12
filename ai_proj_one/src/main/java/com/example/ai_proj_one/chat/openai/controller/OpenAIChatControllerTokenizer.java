package com.example.ai_proj_one.chat.openai.controller;


import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.ai_proj_one.chat.util.LogUtil.logTokenInfo;
import static com.example.ai_proj_one.utils.Constants.OPENAI_CHAT_CLIENT_QUALIFIER;

@Slf4j
@RestController
@RequestMapping("/api/openai/chat")
public class OpenAIChatControllerTokenizer {

    private final ChatClient chatClient;

    /**
     * mutated to only add simpleLoggerAdvisor, doesn't replace the existing advisors
     * @param chatClient
     * @param simpleLoggerAdvisor
     */
    public OpenAIChatControllerTokenizer(@Qualifier(OPENAI_CHAT_CLIENT_QUALIFIER) ChatClient chatClient,
                                SimpleLoggerAdvisor simpleLoggerAdvisor) {
         this.chatClient = chatClient.mutate().defaultAdvisors(simpleLoggerAdvisor).build();
    }

    @PostMapping("/token-info")
    public ChatClientResponse generalChat(@RequestBody String message) {
        ChatClientResponse chatClientResponse = chatClient.prompt()
                .user(message)
                .call()
                .chatClientResponse();
        logTokenInfo(message, chatClientResponse);
        return chatClientResponse;
    }



    /**
     * can also be added as an advisor
     *
     * However, Docker Model Runner may return model.gguf, which JTokkit won't recognize.
     *
     * A better approach is to configure a model-to-tokenizer mapping rather than
     * hardcoding the encoding in your business logic:
     *
     * jtokkit can't handle gemma, use sentencepiece
     *
     * @param message
     * @param chatClientResponse
     */

}
