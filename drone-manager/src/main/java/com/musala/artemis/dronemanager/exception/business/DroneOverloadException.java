package com.musala.artemis.dronemanager.exception.business;

import com.musala.artemis.dronemanager.model.Drone;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class DroneOverloadException extends ResponseStatusException {
    private static final String MESSAGE_FORMAT = "The total shipment weight = %s gr is more than drone carry limit = %s gr";

    public DroneOverloadException(Double totalWeight, Drone drone) {
        super(HttpStatus.BAD_REQUEST, MESSAGE_FORMAT.formatted(totalWeight, drone.getWeightLimit()));
    }
}
