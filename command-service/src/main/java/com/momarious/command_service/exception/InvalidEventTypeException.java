package com.momarious.command_service.exception;

public class InvalidEventTypeException extends RuntimeException {
    public InvalidEventTypeException() {
    }

    public InvalidEventTypeException(String eventType) {
        super("invalid event type: " + eventType);
    }
}