package com.momarious.query_service.exception;

public class EventProcessingException extends RuntimeException {

  public EventProcessingException(String message, Throwable cause) {
    super(message, cause);
  }

}
