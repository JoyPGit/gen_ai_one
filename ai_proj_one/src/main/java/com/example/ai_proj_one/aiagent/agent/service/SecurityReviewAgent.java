package com.example.ai_proj_one.aiagent.agent.service;

import com.example.ai_proj_one.aiagent.dto.*;

public interface SecurityReviewAgent {

    /**
     * Execute the Plan-then-Act + ReAct loop and return a Markdown report.
     *
     * @param reviewState   The uploaded diagram reviewState
     * @return Markdown report (final)
     */
    String execute(String userGoal, ReviewState reviewState);

    /** Answer a follow-up question using the same conversation (memory) keyed by reviewId. */
    String followUp(String reviewId, String question);
}