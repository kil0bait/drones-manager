package com.musala.artemis.dronemanager.rest.controller;

import com.musala.artemis.dronemanager.rest.model.RestErrorResponse;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;
import java.util.stream.Collectors;

@ControllerAdvice
public class RestExceptionHandler {
    private static final String VALIDATION_MESSAGE_GENERIC_FORMAT = "[cause: %s]";
    private static final String VALIDATION_MESSAGE_FIELD_ERROR_FORMAT = "[field: %s, cause: %s]";

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Object> handleException(DataAccessException e) {
        return ResponseEntity.internalServerError().body(new RestErrorResponse(new Date(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), e.getClass().getSimpleName()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleException(MethodArgumentNotValidException e) {
        String message = e.getAllErrors().stream()
                .map(RestExceptionHandler::objectErrorToString)
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body(new RestErrorResponse(new Date(), HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(), message));
    }

    private static String objectErrorToString(ObjectError objectError) {
        if (objectError instanceof FieldError fieldError)
            return VALIDATION_MESSAGE_FIELD_ERROR_FORMAT.formatted(fieldError.getField(), objectError.getDefaultMessage());
        return VALIDATION_MESSAGE_GENERIC_FORMAT.formatted(objectError.getDefaultMessage());
    }
}
