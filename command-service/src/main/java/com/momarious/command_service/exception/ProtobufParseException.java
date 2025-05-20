package com.momarious.command_service.exception;

public class ProtobufParseException extends RuntimeException {
    public ProtobufParseException(String message, Throwable cause) {
        super(message, cause);
    }
}