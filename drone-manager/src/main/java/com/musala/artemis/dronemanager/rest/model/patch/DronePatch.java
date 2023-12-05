package com.musala.artemis.dronemanager.rest.model.patch;

import com.musala.artemis.dronemanager.model.Drone;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DronePatch {
    private String model;
    @DecimalMin("0")
    private Double weightLimit;
    @DecimalMin("0")
    @DecimalMax("100")
    private Double batteryCapacity;

    public DronePatch(Drone drone) {
        this.model = drone.getModel().getName();
        this.weightLimit = drone.getWeightLimit();
        this.batteryCapacity = drone.getBatteryCapacity();
    }
}
