package com.musala.artemis.dronemanager.service;

import com.musala.artemis.dronemanager.exception.business.BlankAddresseeException;
import com.musala.artemis.dronemanager.exception.business.LowBatteryLevelException;
import com.musala.artemis.dronemanager.exception.business.NotApplicableForStateException;
import com.musala.artemis.dronemanager.model.Drone;
import com.musala.artemis.dronemanager.model.DroneState;
import com.musala.artemis.dronemanager.model.Shipment;
import com.musala.artemis.dronemanager.model.ShipmentState;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShipmentValidation {
    public static final String UPDATE = "UPDATE";
    public static final String START_LOAD = "START LOADING";
    public static final String LOAD = "LOADING";
    public static final String FINISH_LOAD = "FINISH LOADING";
    public static final String START_DELIVERY = "START DELIVERY";
    public static final String FINISH_DELIVERY = "FINISH DELIVERY";
    public static final String START_RETURN = "RETURN";
    public static final String FINISH_RETURN = "FINISH RETURNING";
    public static final String CANCEL = "CANCEL";

    private final Double minBatteryLevelForLoading;

    public void validateUpdate(Shipment shipment) {
        if (shipment.getShipmentState().order > 300)
            throw new NotApplicableForStateException(Shipment.class, UPDATE, shipment.getShipmentState().toString());
    }

    public void validateStartLoading(Shipment shipment, Drone drone) {
        if (shipment.getShipmentState() != ShipmentState.NEW)
            throw new NotApplicableForStateException(Shipment.class, START_LOAD, shipment.getShipmentState().toString());
        if (drone.getState() != DroneState.IDLE)
            throw new NotApplicableForStateException(Drone.class, START_LOAD, drone.getState().toString());
        if (drone.getBatteryCapacity() < minBatteryLevelForLoading)
            throw new LowBatteryLevelException(minBatteryLevelForLoading);
    }

    public void validateLoading(Shipment shipment, Drone drone) {
        if (shipment.getShipmentState() != ShipmentState.LOADING)
            throw new NotApplicableForStateException(Shipment.class, LOAD, shipment.getShipmentState().toString());
        if (drone.getState() != DroneState.LOADING)
            throw new NotApplicableForStateException(Drone.class, LOAD, drone.getState().toString());
    }

    public void validateFinishLoading(Shipment shipment, Drone drone) {
        if (shipment.getShipmentState() != ShipmentState.LOADING)
            throw new NotApplicableForStateException(Shipment.class, FINISH_LOAD, shipment.getShipmentState().toString());
        if (drone.getState() != DroneState.LOADING)
            throw new NotApplicableForStateException(Drone.class, FINISH_LOAD, drone.getState().toString());
    }

    public void validateStartDelivery(Shipment shipment, Drone drone) {
        if (Strings.isBlank(shipment.getAddressee()))
            throw new BlankAddresseeException();
        if (shipment.getShipmentState() != ShipmentState.LOADED)
            throw new NotApplicableForStateException(Shipment.class, START_DELIVERY, shipment.getShipmentState().toString());
        if (drone.getState() != DroneState.LOADED)
            throw new NotApplicableForStateException(Drone.class, START_DELIVERY, drone.getState().toString());
    }

    public void validateFinishDelivery(Shipment shipment, Drone drone) {
        if (shipment.getShipmentState() != ShipmentState.DELIVERING)
            throw new NotApplicableForStateException(Shipment.class, FINISH_DELIVERY, shipment.getShipmentState().toString());
        if (drone.getState() != DroneState.DELIVERING)
            throw new NotApplicableForStateException(Drone.class, FINISH_DELIVERY, drone.getState().toString());
    }

    public void validateCancel(Shipment shipment, Drone drone) {
        if (shipment.getShipmentState().order >= 500)
            throw new NotApplicableForStateException(Shipment.class, CANCEL, shipment.getShipmentState().toString());
        if (drone.getState().order >= 500)
            throw new NotApplicableForStateException(Drone.class, CANCEL, drone.getState().toString());
    }

    public void validateDroneReturn(@Nullable Shipment shipment, Drone drone) {
        if (shipment != null && shipment.getShipmentState() != ShipmentState.CANCELLED_RETURNING)
            throw new NotApplicableForStateException(Shipment.class, START_RETURN, shipment.getShipmentState().toString());
        if (drone.getState() != DroneState.DELIVERING && drone.getState() != DroneState.DELIVERED)
            throw new NotApplicableForStateException(Drone.class, START_RETURN, drone.getState().toString());
    }

    public void validateDroneFinishReturn(@Nullable Shipment shipment, Drone drone) {
        if (shipment != null && shipment.getShipmentState() != ShipmentState.CANCELLED_RETURNING)
            throw new NotApplicableForStateException(Shipment.class, FINISH_RETURN, shipment.getShipmentState().toString());
        if (drone.getState() != DroneState.RETURNING)
            throw new NotApplicableForStateException(Drone.class, FINISH_RETURN, drone.getState().toString());
    }
}
