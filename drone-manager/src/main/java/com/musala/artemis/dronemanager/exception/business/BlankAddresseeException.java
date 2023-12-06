package com.musala.artemis.dronemanager.exception.business;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class BlankAddresseeException extends ResponseStatusException {
    private static final String MESSAGE = "Cannot start delivery since shipment addressee is not specified";

    public BlankAddresseeException() {
        super(HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
