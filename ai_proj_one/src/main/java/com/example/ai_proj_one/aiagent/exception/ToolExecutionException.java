package com.example.ai_proj_one.aiagent.exception;

public class ToolExecutionException extends RuntimeException {

    private final String toolName;
    private final String errorCode;
    private final String detail;

    public ToolExecutionException(String toolName, String errorCode, String detail) {
        super("%s failed: %s - %s".formatted(toolName, errorCode, detail));
        this.toolName = toolName;
        this.errorCode = errorCode;
        this.detail = detail;
    }
}
