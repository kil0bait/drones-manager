package com.musala.artemis.dronemanager.exception.business;

import com.musala.artemis.dronemanager.model.Drone;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ExistingShipmentException extends ResponseStatusException {
    private static final String MESSAGE_FORMAT = "The drone with id = %s has an active shipment";

    public ExistingShipmentException(Drone drone) {
        super(HttpStatus.CONFLICT, MESSAGE_FORMAT.formatted(drone.getId()));
    }
}
