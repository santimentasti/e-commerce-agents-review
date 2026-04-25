package com.ecommerce.api.controller;

import com.ecommerce.api.dto.ErrorResponse;
import com.ecommerce.domain.exception.CustomerNotFoundException;
import com.ecommerce.domain.exception.DomainException;
import com.ecommerce.domain.exception.InsufficientStockException;
import com.ecommerce.domain.exception.OrderNotFoundException;
import com.ecommerce.domain.exception.ProductNotFoundException;
import java.time.Instant;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler({ProductNotFoundException.class,
      CustomerNotFoundException.class, OrderNotFoundException.class})
  public ResponseEntity<ErrorResponse> handleNotFound(DomainException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ErrorResponse(404, "Not Found", ex.getMessage(), Instant.now()));
  }

  @ExceptionHandler(InsufficientStockException.class)
  public ResponseEntity<ErrorResponse> handleInsufficientStock(InsufficientStockException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(new ErrorResponse(409, "Conflict", ex.getMessage(), Instant.now()));
  }

  @ExceptionHandler(DomainException.class)
  public ResponseEntity<ErrorResponse> handleDomain(DomainException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(400, "Bad Request", ex.getMessage(), Instant.now()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
    String message = ex.getBindingResult().getFieldErrors().stream()
        .map(FieldError::getDefaultMessage)
        .collect(Collectors.joining("; "));
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(400, "Validation Failed", message, Instant.now()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorResponse(500, "Internal Server Error",
            "An unexpected error occurred", Instant.now()));
  }
}
