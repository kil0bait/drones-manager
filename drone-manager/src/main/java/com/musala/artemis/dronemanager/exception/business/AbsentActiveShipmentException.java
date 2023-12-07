package com.musala.artemis.dronemanager.exception.business;

import com.musala.artemis.dronemanager.model.Drone;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AbsentActiveShipmentException extends ResponseStatusException {
    private static final String MESSAGE_FORMAT = "No active shipment for a drone %s";

    public AbsentActiveShipmentException(Drone drone) {
        super(HttpStatus.NOT_FOUND, MESSAGE_FORMAT.formatted(drone.getId()));
    }
}
