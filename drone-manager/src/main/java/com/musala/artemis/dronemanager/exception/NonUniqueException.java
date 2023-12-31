package com.musala.artemis.dronemanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NonUniqueException extends ResponseStatusException {
    private static final String MESSAGE_FORMAT = "%s with %s = %s already exists";

    public <T> NonUniqueException(Class<T> aClass, String key, String value) {
        super(HttpStatus.CONFLICT, MESSAGE_FORMAT.formatted(aClass.getSimpleName(), key, value));
    }
}
