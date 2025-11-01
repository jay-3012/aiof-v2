package com.tech2nxt.aiofbackend.exception;

class ResourceNotFoundException extends AiofException {
    public ResourceNotFoundException(String resource, String field, Object value) {
        super(String.format("%s not found with %s: %s", resource, field, value));
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}