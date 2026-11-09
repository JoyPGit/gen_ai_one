package com.example.ai_proj_one.chat.openai.controller;

import com.example.ai_proj_one.chat.openai.dto.response.SummarizationResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.example.ai_proj_one.chat.openai.controller.OpenAIChatController.SYSTEM_PROMPT;
import static com.example.ai_proj_one.utils.Constants.OPENAI_CHAT_CLIENT_QUALIFIER;

@RestController
@RequestMapping("/api/openai/chat/structured")
public class OpenAIChatControllerStructured {

    private final ChatClient chatClient;

    OpenAIChatControllerStructured(@Qualifier(OPENAI_CHAT_CLIENT_QUALIFIER) ChatClient chatClient){
        this.chatClient = chatClient;
    }

    /**
     * ParametrizedTypeReference
     * .entity(new ParameterizedTypeReference<>() {});
     * if passed .entity(new ParameterizedTypeReference<SummarizationResponse>() {});
     * -> Collections.singletonList -> single entry
     *
     * @param meetingNotes
     * @return
     */
    @PostMapping("/summarize-meeting-notes-structured-list")
    public List<SummarizationResponse> summarizeMeetingNotesStructuredOutputList(@RequestBody String meetingNotes) {
        try {
            return chatClient.prompt()
                    .system(SYSTEM_PROMPT)
                    .user(u -> u
                            .text("""
                                    Can you summarize the following notes?
                                    Give me three different summarization response in same format as options to choose from. 
                                    Use the format demonstrated in this example:
                                    
                                    Input:
                                    In today's sales strategy meeting, we reviewed Q3 targets
                                    and performance gaps. A proposal was made to expand into
                                    two new regions. Marketing suggested aligning.
                                    
                                    Output:
                                    Action items:
                                    * Focus on enterprise clients and partnerships
                                    * Explore expansion into two new regions
                                    * Align marketing campaigns with sales objectives
                                    
                                    Decisions:
                                    Enterprise clients prioritized for Q3.
                                    Marketing and sales to work jointly on lead conversion.
                                    
                                    Meeting notes:
                                    {meetingNotes}
                                    """)
                            .param("meetingNotes", meetingNotes))
                    .call()
                    .entity(new ParameterizedTypeReference<>() {
                    });
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    @PostMapping("/list")
    public List<String> structuredList(@RequestBody String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .entity(new ListOutputConverter());

    }

    @PostMapping("/map")
    public Map<String, Object> structuredMap(@RequestBody String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .entity(new MapOutputConverter());

    }
}
