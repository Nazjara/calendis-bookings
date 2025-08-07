package com.nazjara.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

/**
 * Global exception handler for the application. Provides centralized exception handling across all
 * controllers.
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Handles authentication exceptions.
   *
   * @param ex      the exception
   * @param request the current request
   * @return a ResponseEntity with error details
   */
  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex,
      HttpServletRequest request) {
    log.error("Authentication exception: {}", ex.getMessage(), ex);

    var errorResponse = buildErrorResponse(
        HttpStatus.UNAUTHORIZED,
        "Authentication failed",
        ex.getMessage(),
        request.getRequestURI()
    );

    return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
  }

  /**
   * Handles appointment not found exceptions.
   *
   * @param ex      the exception
   * @param request the current request
   * @return a ResponseEntity with error details
   */
  @ExceptionHandler(AppointmentNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleAppointmentNotFoundException(
      AppointmentNotFoundException ex, HttpServletRequest request) {
    log.error("Appointment not found: {}", ex.getMessage(), ex);

    var errorResponse = buildErrorResponse(
        HttpStatus.NOT_FOUND,
        "Appointment not found",
        ex.getMessage(),
        request.getRequestURI()
    );

    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
  }

  /**
   * Handles booking exceptions.
   *
   * @param ex      the exception
   * @param request the current request
   * @return a ResponseEntity with error details
   */
  @ExceptionHandler(BookingException.class)
  public ResponseEntity<ErrorResponse> handleBookingException(BookingException ex,
      HttpServletRequest request) {
    log.error("Booking exception: {}", ex.getMessage(), ex);

    var errorResponse = buildErrorResponse(
        HttpStatus.BAD_REQUEST,
        "Booking failed",
        ex.getMessage(),
        request.getRequestURI()
    );

    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handles HTTP client errors from RestClient.
   *
   * @param ex      the exception
   * @param request the current request
   * @return a ResponseEntity with error details
   */
  @ExceptionHandler(HttpClientErrorException.class)
  public ResponseEntity<ErrorResponse> handleHttpClientErrorException(HttpClientErrorException ex,
      HttpServletRequest request) {
    log.error("HTTP client error: {}", ex.getMessage(), ex);

    var errorResponse = buildErrorResponse(
        HttpStatus.valueOf(ex.getStatusCode().value()),
        "External API error",
        ex.getMessage(),
        request.getRequestURI()
    );

    return new ResponseEntity<>(errorResponse, ex.getStatusCode());
  }

  /**
   * Handles resource access exceptions (connection issues, timeouts, etc.).
   *
   * @param ex      the exception
   * @param request the current request
   * @return a ResponseEntity with error details
   */
  @ExceptionHandler(ResourceAccessException.class)
  public ResponseEntity<ErrorResponse> handleResourceAccessException(ResourceAccessException ex,
      HttpServletRequest request) {
    log.error("Resource access exception: {}", ex.getMessage(), ex);

    var errorResponse = buildErrorResponse(
        HttpStatus.SERVICE_UNAVAILABLE,
        "External service unavailable",
        "Unable to connect to external service: " + ex.getMessage(),
        request.getRequestURI()
    );

    return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
  }

  /**
   * Handles all other exceptions.
   *
   * @param ex      the exception
   * @param request the current request
   * @return a ResponseEntity with error details
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(Exception ex,
      HttpServletRequest request) {
    log.error("Unexpected error: {}", ex.getMessage(), ex);

    var errorResponse = buildErrorResponse(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "Internal server error",
        "An unexpected error occurred: " + ex.getMessage(),
        request.getRequestURI()
    );

    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   * Builds a standardized error response.
   *
   * @param status  the HTTP status
   * @param error   the error type
   * @param message the error message
   * @param path    the request path
   * @return the error response
   */
  private ErrorResponse buildErrorResponse(HttpStatus status, String error, String message,
      String path) {
    return ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(status.value())
        .error(error)
        .message(message)
        .path(path)
        .build();
  }
}