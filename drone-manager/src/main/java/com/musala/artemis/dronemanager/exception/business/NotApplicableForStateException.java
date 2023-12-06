package com.musala.artemis.dronemanager.exception.business;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NotApplicableForStateException extends ResponseStatusException {
    private static final String MESSAGE_FORMAT = "Cannot perform %s %s for state = %s";

    public <T> NotApplicableForStateException(Class<T> tClass, String operation, String state) {
        super(HttpStatus.BAD_REQUEST, MESSAGE_FORMAT.formatted(operation, tClass.getSimpleName(), state));
    }
}
