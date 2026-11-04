package com.example.ai_proj_one.controller;

import com.example.ai_proj_one.service.OpenAILocalService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import static com.example.ai_proj_one.utils.Constants.OPENAI_CHAT_CLIENT_QUALIFIER;

/**
 * openai chat client via qualifier
 *
 */
@RestController
@RequestMapping("/ai/openai/chat")
public class OpenAIChatController {
    private final static String SYSTEM_PROMPT = "You are a helpful assistant thats " +
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
    @PostMapping("summarize-meeting-notes")
    public String summarizeMeetingNotes(@RequestBody String meetingNotes) {
        return chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(u -> u.text("Web search Can you summarize the following notes " +
                        "{meeting notes}? Use the format as described in the following " +
                        "example while doing the summarization. Input: In today's sales" +
                                " strategy meeting, we reviewed Q3 targets and " +
                                "performance gaps. A proposal was made to expand into " +
                                "two new regions. Marketing suggested aligning. Output: Action items: " +
                        "* Focus on enterprise clients and partnerships" +
                        "*Explore expansion into two new regions" +
                        "*Align marketing campaigns with sales objectives" +
                        "Decisions: enterprise clients prioritized for Q3, marketing " +
                        "and sales to work jointly on lead conversion.")
                        .param("meetingNotes", meetingNotes))
                .call()
                .content();
    }
}
