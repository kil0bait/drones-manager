package com.musala.artemis.dronemanager.exception.business;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class LowBatteryLevelException extends ResponseStatusException {
    private static final String MESSAGE_FORMAT = "Battery capacity of the drone is less than MIN required level = %s";

    public LowBatteryLevelException(Double minBatteryLevel) {
        super(HttpStatus.BAD_REQUEST, MESSAGE_FORMAT.formatted(minBatteryLevel));
    }
}
