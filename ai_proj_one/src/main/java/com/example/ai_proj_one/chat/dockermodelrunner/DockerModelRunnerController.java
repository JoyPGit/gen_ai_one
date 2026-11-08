package com.example.ai_proj_one.chat.dockermodelrunner;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import static com.example.ai_proj_one.utils.Constants.*;

/**
 * reusing openai chat client via qualifier
 * with diff config vals, base-url and model (both of local docker model)
 * append /v1 to the base url, required with spring ai 2.0
 * docker model list -> pick model name
 *
 *
 * No, you do not need to add a different dependency or use a generic client.
 * The standard Spring AI OpenAI starter dependency (spring-ai-openai-spring-boot-starter)
 * is explicitly designed to handle OpenAI-compatible endpoints
 * like Docker Model Runner, Ollama, vLLM, or LM Studio.
 */
@RestController
@RequestMapping("/api/docker-model-runner/chat")
public class DockerModelRunnerController {

    private final static String SYSTEM_PROMPT = "You are a helpful assistant that generates " +
            "professional LinkedIn posts about technical subjects. " +
            "Ensure the posts are engaging, informative and tailored to a " +
            "professional audience, user-friendly and approachable tone " +
            "while maintaining professionalism.";

    private final static String SYSTEM_PROMPT_LAW = """
            You are a helpful assistant that has a degree in law.
            Ensure the content which I ask for is from the Indian Contract Act
            and tailored to a professional audience, user-friendly and approachable tone
            while maintaining professionalism.
            """;

    private final ChatClient chatClient;

    public DockerModelRunnerController(@Qualifier(OPENAI_CHAT_CLIENT_QUALIFIER) ChatClient chatClient) {
        this.chatClient = chatClient;
    }


    @PostMapping("/linked-post-generator")
    public String linkedPostGenerator(@RequestBody String message) {
        return chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(message)
                .advisors()
                .call()
                .content();
    }

    @PostMapping("/law-details")
    public String lawDetails(@RequestBody String message) {
        return chatClient.prompt()
                .system(SYSTEM_PROMPT_LAW)
                .user(message)
                .call()
                .content();
    }

    /**
     * now add 2 endpoints
     * 1 to plan how to process pics which i send and extract data
     * 2 how to throttle batch upload so that it doesn't overwhelm the system
     */

}
