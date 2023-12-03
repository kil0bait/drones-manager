package com.musala.artemis.dronemanager.exception.business;

import com.musala.artemis.dronemanager.model.DroneModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NotValidWeightLimitException extends ResponseStatusException {
    private static final String MESSAGE_FORMAT = "Weight limit must be less or equal than %s limit = %s gr";

    public NotValidWeightLimitException(DroneModel model) {
        super(HttpStatus.BAD_REQUEST, MESSAGE_FORMAT.formatted(model.getName(), model.getDefaultWeightLimit()));
    }
}
