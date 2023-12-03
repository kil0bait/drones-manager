package com.musala.artemis.dronemanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class RelationNotFoundException extends ResponseStatusException {
    private static final String NOT_FOUND_DETAILED_MESSAGE_FORMAT = "%s with %s = %s not found";

    public <T> RelationNotFoundException(Class<T> aClass, String key, String value) {
        super(HttpStatus.BAD_REQUEST, NOT_FOUND_DETAILED_MESSAGE_FORMAT.formatted(aClass.getSimpleName(), key, value));
    }
}
