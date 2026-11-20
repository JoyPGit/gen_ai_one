package com.example.ai_proj_one.rag.exception;

public class RagException extends RuntimeException{

    public RagException(String message) {
        super(message);
    }

    public RagException(String message, Throwable cause) {
        super(message, cause);
    }
}
