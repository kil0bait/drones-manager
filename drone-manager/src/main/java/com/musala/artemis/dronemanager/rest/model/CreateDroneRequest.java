package com.musala.artemis.dronemanager.rest.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateDroneRequest {
    @NotBlank
    @Size(max = 100)
    @Schema(description = "Serial number")
    private String serialNumber;
    @NotBlank
    @Schema(description = "Drone model name")
    private String model;
    @Schema(description = "Weight limit in grams")
    private Double weightLimit;
    @Schema(description = "Battery capacity in percents, e.g. 56.5")
    @DecimalMin("0")
    @DecimalMax("100")
    private Double batteryCapacity;
}
