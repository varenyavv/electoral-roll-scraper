package com.raw.scraper.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.raw.scraper.constant.NepalState;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(AppExceptionHandler.class);

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<String> handleMethodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException ex) {
    LOG.warn("handleMethodArgumentTypeMismatchException", ex);
    String message = "";
    if ("state".equals(ex.getName())) {
      message = "Invalid state. Valid states are " + Arrays.toString(NepalState.values());
    }
    if ("district".equals(ex.getName())) {
      message = "Value of district should be numeric. Please provide numeric district code";
    }
    return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(JsonProcessingException.class)
  public ResponseEntity<String> handleJsonProcessingException(JsonProcessingException ex) {
    LOG.warn("handleJsonProcessingException", ex);
    return new ResponseEntity<>("Unable to parse API Response", HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(AppException.class)
  public ResponseEntity<String> handleAppException(AppException ex) {
    LOG.warn("handleExecutionException", ex);
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleUnhandledException(Exception ex) {
    LOG.warn("handleUnhandledException", ex);
    return new ResponseEntity<>(
        String.format("Internal error occurred [%s]", ex.getMessage()),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(ExecutionException.class)
  public ResponseEntity<String> handleExecutionException(ExecutionException ex) {
    LOG.warn("handleExecutionException", ex);
    return new ResponseEntity<>("Error generating CSV", HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(InterruptedException.class)
  public ResponseEntity<String> handleInterruptedException(InterruptedException ex) {
    LOG.warn("handleUnhandledException", ex);
    return new ResponseEntity<>("Error generating CSV", HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
