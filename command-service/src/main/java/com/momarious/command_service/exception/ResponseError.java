package com.momarious.command_service.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseError {

  private HttpStatus status;
  private String message;
  private List<String> errors;

  public ResponseError(HttpStatus status, String message) {
    this.status = status;
    this.message = message;
  }
}
