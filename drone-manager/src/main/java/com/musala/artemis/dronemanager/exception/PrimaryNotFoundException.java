package com.musala.artemis.dronemanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class PrimaryNotFoundException extends ResponseStatusException {
    private static final String NOT_FOUND_DETAILED_MESSAGE_FORMAT = "%s with %s = %s not found";

    public <T> PrimaryNotFoundException(Class<T> aClass, String key, String value) {
        super(HttpStatus.NOT_FOUND, NOT_FOUND_DETAILED_MESSAGE_FORMAT.formatted(aClass.getSimpleName(), key, value));
    }
}
