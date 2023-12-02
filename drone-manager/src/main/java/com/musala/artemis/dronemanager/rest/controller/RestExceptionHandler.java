package com.musala.artemis.dronemanager.rest.controller;

import com.musala.artemis.dronemanager.exception.GenericException;
import com.musala.artemis.dronemanager.exception.PrimaryNotFoundException;
import com.musala.artemis.dronemanager.rest.model.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
public class RestExceptionHandler {
    private static final String VALIDATION_MESSAGE_GENERIC_FORMAT = "[cause: %s]";
    private static final String VALIDATION_MESSAGE_FIELD_ERROR_FORMAT = "[field: %s, cause: %s]";

    @ExceptionHandler(PrimaryNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(PrimaryNotFoundException e) {
        return ResponseEntity.status(NOT_FOUND).body(new ErrorResponse(e.getMessage(), e.getDetailedMessage()));
    }

    @ExceptionHandler(GenericException.class)
    public ResponseEntity<ErrorResponse> handleException(GenericException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), e.getDetailedMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        return ResponseEntity.internalServerError().body(new ErrorResponse(e.getClass().getSimpleName(), e.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleException(HttpMessageNotReadableException e) {
        return ResponseEntity.internalServerError().body(new ErrorResponse(e.getClass().getSimpleName(), e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleException(MethodArgumentNotValidException e) {
        String error = "Payload not valid";
        String message = e.getAllErrors().stream()
                .map(RestExceptionHandler::objectErrorToString)
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body(new ErrorResponse(error, message));
    }

    private static String objectErrorToString(ObjectError objectError) {
        if (objectError instanceof FieldError fieldError)
            return VALIDATION_MESSAGE_FIELD_ERROR_FORMAT.formatted(fieldError.getField(), objectError.getDefaultMessage());
        return VALIDATION_MESSAGE_GENERIC_FORMAT.formatted(objectError.getDefaultMessage());
    }
}
