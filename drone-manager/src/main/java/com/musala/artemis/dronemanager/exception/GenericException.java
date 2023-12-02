package com.musala.artemis.dronemanager.exception;

import lombok.Getter;

@Getter
public class GenericException extends Exception {
    private final String message;
    private final String detailedMessage;

    public GenericException(String message, String detailedMessage) {
        super(message);
        this.message = message;
        this.detailedMessage = detailedMessage;
    }
}
