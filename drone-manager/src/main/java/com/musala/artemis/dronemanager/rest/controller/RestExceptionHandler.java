package com.musala.artemis.dronemanager.rest.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.musala.artemis.dronemanager.exception.PatchValidationException;
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
import java.util.List;
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

    @ExceptionHandler({JsonPatchException.class, JsonProcessingException.class})
    public ResponseEntity<Object> handleException(Exception e) {
        return ResponseEntity.badRequest().body(new RestErrorResponse(new Date(), HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleException(MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest().body(new RestErrorResponse(new Date(), HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(), validationMessage(e.getAllErrors())));
    }

    @ExceptionHandler(PatchValidationException.class)
    public ResponseEntity<Object> handleException(PatchValidationException e) {
        return ResponseEntity.badRequest().body(new RestErrorResponse(new Date(), HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(), validationMessage(e.getErrors().getAllErrors())));
    }

    private static String validationMessage(List<ObjectError> objectErrors) {
        return objectErrors.stream()
                .map(RestExceptionHandler::objectErrorToString)
                .collect(Collectors.joining(", "));
    }

    private static String objectErrorToString(ObjectError objectError) {
        if (objectError instanceof FieldError fieldError)
            return VALIDATION_MESSAGE_FIELD_ERROR_FORMAT.formatted(fieldError.getField(), objectError.getDefaultMessage());
        return VALIDATION_MESSAGE_GENERIC_FORMAT.formatted(objectError.getDefaultMessage());
    }
}
