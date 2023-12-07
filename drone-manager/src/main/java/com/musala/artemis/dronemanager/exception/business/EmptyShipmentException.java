package com.musala.artemis.dronemanager.exception.business;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class EmptyShipmentException extends ResponseStatusException {
    private static final String MESSAGE = "Cannot continue. The shipment is empty";

    public EmptyShipmentException() {
        super(HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
