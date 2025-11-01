package com.tech2nxt.aiofbackend.exception;

public class AiofException extends RuntimeException {
    public AiofException(String message) {
        super(message);
    }

    public AiofException(String message, Throwable cause) {
        super(message, cause);
    }
}
