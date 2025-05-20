package com.momarious.query_service.exception;

public class ResourceNotFoundException extends RuntimeException {

  private static final String MESSAGE = "%s not found with %s: '%s'";

  public ResourceNotFoundException(String resource , String field, Object value) {
    super(String.format(MESSAGE, resource, field, value));
  }
}
