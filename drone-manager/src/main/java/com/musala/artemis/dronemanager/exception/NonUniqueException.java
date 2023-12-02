package com.musala.artemis.dronemanager.exception;

public class NonUniqueException extends GenericException{
    private static final String NOT_FOUND_MESSAGE = "Non-unique error";
    private static final String NOT_FOUND_DETAILED_MESSAGE_FORMAT = "%s with %s = %s already exists";

    public <T> NonUniqueException(Class<T> aClass, String key, String value) {
        super(NOT_FOUND_MESSAGE, NOT_FOUND_DETAILED_MESSAGE_FORMAT.formatted(aClass.getSimpleName(), key, value));
    }
}
