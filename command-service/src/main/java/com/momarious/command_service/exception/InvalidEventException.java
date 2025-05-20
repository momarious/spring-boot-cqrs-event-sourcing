package com.momarious.command_service.exception;

public class InvalidEventException extends RuntimeException {
  public InvalidEventException() {
  }

  public InvalidEventException(String message) {
    super("invalid event: " + message);
  }
}
