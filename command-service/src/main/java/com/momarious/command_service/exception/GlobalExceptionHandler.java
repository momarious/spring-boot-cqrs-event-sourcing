package com.momarious.command_service.exception;

import java.text.MessageFormat;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.resource.NoResourceFoundException;

// import io.sentry.Sentry;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(MethodArgumentNotValidException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ResponseEntity<ResponseError> handleMethodArgumentNotValidException(
                        MethodArgumentNotValidException e) {
                // Sentry.captureException(e);
                List<String> errors = e.getBindingResult().getFieldErrors().stream()
                                .map(f -> MessageFormat.format("{0}: {1}", f.getField(),
                                                f.getDefaultMessage()))
                                .toList();
                ResponseError response = new ResponseError(HttpStatus.BAD_REQUEST,
                                e.getBody().getDetail());
                response.setErrors(errors);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler({ NoResourceFoundException.class,
                        AggregateStateException.class, AggregateNotFoundException.class, BadRequestException.class })
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ResponseEntity<ResponseError> handleBadRequestExceptions(Exception e) {
                // Sentry.captureException(e);
                ResponseError response = new ResponseError(HttpStatus.BAD_REQUEST,
                                e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        @ExceptionHandler(JsonSerDeException.class)
        public ResponseEntity<ResponseError> handleDeserializationException(JsonSerDeException ex) {

                ResponseError response = new ResponseError(HttpStatus.BAD_REQUEST,
                                "Deserialization error: " + ex.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        @ExceptionHandler(InvalidEventTypeException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ResponseEntity<ResponseError> handleUnsupportedEventType(InvalidEventTypeException e) {
                ResponseError response = new ResponseError(HttpStatus.BAD_REQUEST, e.getMessage());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

}
