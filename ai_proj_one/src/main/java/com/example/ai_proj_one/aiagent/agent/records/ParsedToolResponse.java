package com.example.ai_proj_one.aiagent.agent.records;

import java.util.Map;

public record ParsedToolResponse(Map<String,Object> payload, boolean isError) {}
