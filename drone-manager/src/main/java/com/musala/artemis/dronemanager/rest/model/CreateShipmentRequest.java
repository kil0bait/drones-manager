package com.musala.artemis.dronemanager.rest.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateShipmentRequest {
    @NotBlank
    @Schema(description = "Addressee name and address")
    private String addressee;
    @NotNull
    @Schema(description = "Delivery drone id")
    private Long droneId;
}
