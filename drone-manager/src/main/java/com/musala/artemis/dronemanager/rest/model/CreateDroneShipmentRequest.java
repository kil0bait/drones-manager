package com.musala.artemis.dronemanager.rest.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateDroneShipmentRequest {
    @Schema(description = "Addressee name and address")
    private String addressee;
}
