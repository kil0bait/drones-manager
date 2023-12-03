package com.musala.artemis.dronemanager.rest.model;

import com.musala.artemis.dronemanager.model.Drone;
import com.musala.artemis.dronemanager.model.DroneState;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DroneResponse {
    @NotNull
    @Schema(description = "Drone ID")
    private Long id;
    @NotBlank
    @Size(max = 100)
    @Schema(description = "Serial number. Is unique for each drone")
    private String serialNumber;
    @NotBlank
    @Schema(description = "Drone model name")
    private String modelName;
    @DecimalMin("0")
    @Schema(description = "Weight limit in grams")
    private Double weightLimit;
    @DecimalMin("0")
    @DecimalMax("100")
    @Schema(description = "Battery capacity in percents, e.g. 56.5")
    private Double batteryCapacity;
    @Schema(description = "Drone state")
    private DroneState state;

    public DroneResponse(Drone drone) {
        this.id = drone.getId();
        this.serialNumber = drone.getSerialNumber();
        this.modelName = drone.getModel().getName();
        this.weightLimit = drone.getWeightLimit();
        this.batteryCapacity = drone.getBatteryCapacity();
        this.state = drone.getState();
    }
}
