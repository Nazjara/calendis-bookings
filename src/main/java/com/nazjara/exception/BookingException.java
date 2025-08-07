package com.nazjara.exception;

/**
 * Exception thrown when there is an error during the booking process.
 */
public class BookingException extends RuntimeException {

  public BookingException(String message, Throwable cause) {
    super(message, cause);
  }
}