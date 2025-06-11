package org.example.backend.exceptions;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.example.backend.exceptions.custom.EntityNullException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the application.
 * This class provides centralized exception handling across all controllers.
 * It converts various exceptions into appropriate HTTP responses with meaningful error messages.
 *
 * The handler manages the following types of exceptions:
 * - ConstraintViolationException: For validation errors (e.g., incorrect email format)
 * - EntityNullException: For null entity or field errors
 * - EntityExistsException: For duplicate entity errors
 * - EntityNotFoundException: For missing entity errors
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles validation and null entity exceptions.
     * Returns HTTP 406 (Not Acceptable) status code.
     *
     * @param e the exception to handle (ConstraintViolationException or EntityNullException)
     * @return ResponseEntity containing error details
     */
    @ExceptionHandler({ConstraintViolationException.class, EntityNullException.class})
    public ResponseEntity<Object> handleEntityNullException(Exception e) {
        return buildResponse(e, HttpStatus.NOT_ACCEPTABLE, e.getMessage());
    }

    /**
     * Handles duplicate entity exceptions.
     * Returns HTTP 409 (Conflict) status code.
     *
     * @param e the EntityExistsException to handle
     * @return ResponseEntity containing error details
     */
    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<Object> handleEntityExistsException(EntityExistsException e) {
        return buildResponse(e, HttpStatus.CONFLICT, e.getMessage());
    }

    /**
     * Handles missing entity exceptions.
     * Returns HTTP 404 (Not Found) status code.
     *
     * @param e the EntityNotFoundException to handle
     * @return ResponseEntity containing error details
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException e) {
        return buildResponse(e, HttpStatus.NOT_FOUND, e.getMessage());
    }

    /**
     * Builds a standardized error response.
     * Creates a response body with status code, error message, and additional details.
     *
     * @param e the exception that occurred
     * @param status the HTTP status code to return
     * @param message the error message to include
     * @return ResponseEntity containing the formatted error response
     */
    private ResponseEntity<Object> buildResponse(Exception e, HttpStatus status, String message) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("status", status.value());
        responseBody.put("error", status.getReasonPhrase());
        /*
         Developing logic with this error results in much less code writing, just overriding the error message.
         ConstraintViolationException occurs in case of incorrect user's email formatting, it works through
         @Email annotation.
         */
        if(e instanceof ConstraintViolationException) {
            responseBody.put("message", "Incorrect email formatting. Try next pattern: 'some_information@mail.com'");
        } else
            responseBody.put("message", message);
        return new ResponseEntity<>(responseBody, status);
    }
}
