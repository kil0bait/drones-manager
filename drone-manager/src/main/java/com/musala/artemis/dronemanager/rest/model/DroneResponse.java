package com.musala.artemis.dronemanager.rest.model;

import com.musala.artemis.dronemanager.model.Drone;
import com.musala.artemis.dronemanager.model.DroneState;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DroneResponse {
    private Long id;
    private String serialNumber;
    private String modelName;
    private Double weightLimit;
    private Double batteryCapacity;
    private DroneState state;

    public DroneResponse() {
    }

    public DroneResponse(Drone drone) {
        this.id = drone.getId();
        this.serialNumber = drone.getSerialNumber();
        this.modelName = drone.getModel().getName();
        this.weightLimit = drone.getWeightLimit();
        this.batteryCapacity = drone.getBatteryCapacity();
        this.state = drone.getState();
    }
}
