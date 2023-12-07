package com.musala.artemis.dronemanager.exception.business;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class FleetOverflowException extends ResponseStatusException {
    private static final String MESSAGE_FORMAT = "Cannot add new drone since the fleet size reached the maximum of %s";

    public FleetOverflowException(long maxFleetSize) {
        super(HttpStatus.BAD_REQUEST, MESSAGE_FORMAT.formatted(maxFleetSize));
    }
}
