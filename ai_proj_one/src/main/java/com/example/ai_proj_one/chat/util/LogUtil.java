package com.example.ai_proj_one.chat.util;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.stereotype.Service;

@Slf4j
public class LogUtil {


    /**
     * using JTokkit to encode and check tokens
     * @param message
     * @param chatClientResponse
     */
    public static void logTokenInfo(String message, ChatClientResponse chatClientResponse) {
        EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();
        Encoding encoding = registry.getEncodingForModel(
                chatClientResponse.chatResponse()
                        .getMetadata().getModel()).orElse(null);

        if (encoding == null) {
            log.warn("Encoding not found for model: {}", chatClientResponse.chatResponse().getMetadata().getModel());
            return;
        }

        int inputTokenNumber = encoding.countTokens(message);
        log.info("Input token number: " + inputTokenNumber);

        int outputTokenNumber = encoding.countTokens(chatClientResponse.chatResponse().getResult().getOutput().getText());
        log.info("Output token number: " + outputTokenNumber);
    }
}
