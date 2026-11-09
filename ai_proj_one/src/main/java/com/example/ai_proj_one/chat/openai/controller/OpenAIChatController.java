package com.example.ai_proj_one.chat.openai.controller;

import com.example.ai_proj_one.chat.openai.dto.response.SummarizationResponse;
import com.example.ai_proj_one.chat.openai.service.OpenAILocalService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.example.ai_proj_one.utils.Constants.OPENAI_CHAT_CLIENT_QUALIFIER;

/**
 * openai chat client via qualifier
 *
 */
@RestController
@RequestMapping("/api/openai/chat")
public class OpenAIChatController {
    protected final static String SYSTEM_PROMPT = "You are a helpful assistant that " +
            "summarizes any given content. Ensure the summary is concise, informative " +
            "and captures the key points. Use a friendly and approachable tone while " +
            "maintaining professionalism. DO NOT answer anything other than the " +
            "summarization. If the ask is not related to summarization, respond with 'I" +
            " can only help with summarization";


    private final ChatClient chatClient;
    private final OpenAILocalService openAiService;

    public OpenAIChatController(@Qualifier(OPENAI_CHAT_CLIENT_QUALIFIER) ChatClient chatClient,
            OpenAILocalService openAiService) {
        this.chatClient = chatClient;
        this.openAiService = openAiService;
    }


    /**
     * prompt returns ChatClientRequestSpec
     * system -> to set system msg
     * user -> to send user msg
     * @param meetingNotes
     * @return
     */
    @PostMapping("/summarize-meeting-notes")
    public String summarizeMeetingNotes(@RequestBody String meetingNotes) {
        return chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(u -> u
                        .text("""
            Can you summarize the following notes?

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
                .content();
    }

    /**
     * entity methods accept class type or StructuredOutputConvertor
     * beanOutputConvertor -> String.format(template, this.jsonSchema);
     * @param meetingNotes
     * @return
     */
    @PostMapping("/summarize-meeting-notes-structured")
    public SummarizationResponse summarizeMeetingNotesStructuredOutput(@RequestBody String meetingNotes) {
        return chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(u -> u
                        .text("""
                                Can you summarize the following notes?
                                
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
                .entity(SummarizationResponse.class);
    }


    /**
     * call
     * @param message
     * @return
     */
    @PostMapping("/summarize")
    public String summarize(@RequestBody String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }

    /**
     * call
     * @param message
     * @return
     */
    @PostMapping("/summarize-error")
    public SummarizationResponse summarizeError(@RequestBody String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .entity(SummarizationResponse.class);
    }
}
