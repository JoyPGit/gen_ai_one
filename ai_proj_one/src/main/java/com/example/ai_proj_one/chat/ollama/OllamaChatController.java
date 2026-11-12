package com.example.ai_proj_one.chat.ollama;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import static com.example.ai_proj_one.utils.Constants.OLLAMAAI_CHAT_CLIENT_QUALIFIER;

/**
 * openai chat client via qualifier
 * it requires ollama to run locally brew services start/stop ollama
 * as it's large, it's difficult to run
 */
@RestController
@RequestMapping("/api/ollama/chat")
public class OllamaChatController {

    private final static String SYSTEM_PROMPT = "You are a helpful assistant that " +
            "drafts professional and concise emails based on user input. Ensure the emails " +
            "are clear, polite, and tailored to the specific context. Use a formal and " +
            "respectful tone while maintaining brevity.";
    private final ChatClient chatClient;

    public OllamaChatController(@Qualifier(OLLAMAAI_CHAT_CLIENT_QUALIFIER) ChatClient chatClient) {
        this.chatClient = chatClient;
    }


    @PostMapping("/draft-email")
    public String summarizeMeetingNotes(@RequestBody String message) {
        return chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(message)
                .call()
                .content();
    }

}
