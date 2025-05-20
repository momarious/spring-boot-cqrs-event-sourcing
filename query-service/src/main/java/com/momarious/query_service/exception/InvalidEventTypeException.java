package com.momarious.query_service.exception;

import com.momarious.query_service.enums.EventType;

public class InvalidEventTypeException extends RuntimeException {
    public InvalidEventTypeException() {
    }

    public InvalidEventTypeException(EventType eventType) {
        super("invalid event type: " + eventType);
    }
}