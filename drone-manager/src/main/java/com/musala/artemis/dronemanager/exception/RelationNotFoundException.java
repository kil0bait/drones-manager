package com.musala.artemis.dronemanager.exception;

public class RelationNotFoundException extends GenericException {
    private static final String NOT_FOUND_MESSAGE = "Relation not found";
    private static final String NOT_FOUND_DETAILED_MESSAGE_FORMAT = "%s with %s = %s not found";

    public <T> RelationNotFoundException(Class<T> aClass, String key, String value) {
        super(NOT_FOUND_MESSAGE, NOT_FOUND_DETAILED_MESSAGE_FORMAT.formatted(aClass.getSimpleName(), key, value));
    }
}
