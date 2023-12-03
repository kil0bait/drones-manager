package com.musala.artemis.dronemanager.service.patch;

import com.musala.artemis.dronemanager.model.Drone;
import com.musala.artemis.dronemanager.model.DroneState;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DronePatch {
    private String model;
    @DecimalMin("0")
    private Double weightLimit;
    @DecimalMin("0")
    @DecimalMax("100")
    private Double batteryCapacity;
    private DroneState state;

    public DronePatch(Drone drone) {
        this.model = drone.getModel().getName();
        this.weightLimit = drone.getWeightLimit();
        this.batteryCapacity = drone.getBatteryCapacity();
        this.state = drone.getState();
    }
}
