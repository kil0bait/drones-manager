package com.musala.artemis.dronemanager.service;

import com.musala.artemis.dronemanager.exception.business.FleetOverflowException;
import com.musala.artemis.dronemanager.exception.business.NotApplicableForStateException;
import com.musala.artemis.dronemanager.model.Drone;
import com.musala.artemis.dronemanager.model.DroneState;
import com.musala.artemis.dronemanager.model.Shipment;
import com.musala.artemis.dronemanager.model.ShipmentState;
import org.checkerframework.checker.units.qual.N;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class ValidationServiceTest {
    ValidationService validationService;

    @BeforeEach
    void setUp() {
        validationService = new ValidationService(15.0, 10L);
    }

    @Test
    void validateAddToFleet_success() {
        assertDoesNotThrow(() -> validationService.validateAddToFleet(5L));
    }

    @Test
    void validateAddToFleet_fail() {
        assertThrowsExactly(FleetOverflowException.class, () -> validationService.validateAddToFleet(15L));
    }

    @ParameterizedTest
    @CsvSource({
            "NEW",
            "LOADING",
            "LOADED",
    })
    void validateUpdate_success(ShipmentState state) {
        assertDoesNotThrow(() -> validationService.validateUpdate(shipment(state)));
    }

    @ParameterizedTest
    @CsvSource({
            "DELIVERING",
            "CANCELLED_RETURNING",
            "DELIVERED",
            "CANCELLED",
    })
    void validateUpdate_fail(ShipmentState state) {
        assertThrowsExactly(NotApplicableForStateException.class, () -> validationService.validateUpdate(shipment(state)));
    }

    @ParameterizedTest
    @CsvSource({
            "NEW, IDLE, 100",
            "NEW, IDLE, 25",
            "NEW, IDLE, 15",
    })
    void validateStartLoading_success(ShipmentState shipmentState, DroneState droneState, Double battery) {
        assertDoesNotThrow(() -> validationService.validateStartLoading(shipment(shipmentState), drone(droneState, battery)));
    }

    @ParameterizedTest
    @CsvSource({
            "NEW, IDLE, 10, com.musala.artemis.dronemanager.exception.business.LowBatteryLevelException",
            "NEW, IDLE, 14.9, com.musala.artemis.dronemanager.exception.business.LowBatteryLevelException",
            "LOADING, IDLE, 100, com.musala.artemis.dronemanager.exception.business.NotApplicableForStateException",
            "DELIVERING, IDLE, 100, com.musala.artemis.dronemanager.exception.business.NotApplicableForStateException",
            "CANCELLED, IDLE, 100, com.musala.artemis.dronemanager.exception.business.NotApplicableForStateException",
            "NEW, LOADING, 100, com.musala.artemis.dronemanager.exception.business.NotApplicableForStateException",
            "NEW, DELIVERING, 100, com.musala.artemis.dronemanager.exception.business.NotApplicableForStateException",
    })
    <T extends Throwable> void validateStartLoading_fail(ShipmentState shipmentState, DroneState droneState, Double battery, Class<T> tClass) {
        assertThrowsExactly(tClass, () -> validationService.validateStartLoading(shipment(shipmentState), drone(droneState, battery)));
    }

    @Test
    void validateLoading_success() {
        assertDoesNotThrow(() -> validationService.validateLoading(shipment(ShipmentState.LOADING), drone(DroneState.LOADING, 100.0)));
    }

    @ParameterizedTest
    @CsvSource({
            "NEW, LOADING",
            "DELIVERING, LOADING",
            "CANCELLED, LOADING",
            "LOADING, IDLE",
            "LOADING, DELIVERING",
            "LOADING, RETURNING",
            "CANCELLED, RETURNING",
    })
    void validateLoading_fail(ShipmentState shipmentState, DroneState droneState) {
        assertThrowsExactly(NotApplicableForStateException.class, () -> validationService.validateLoading(shipment(shipmentState), drone(droneState, 100.0)));
    }

    @Test
    void validateFinishLoading() {
    }

    @Test
    void validateStartDelivery() {
    }

    @Test
    void validateFinishDelivery() {
    }

    @Test
    void validateCancel() {
    }

    @Test
    void validateDroneReturn() {
    }

    @Test
    void validateDroneFinishReturn() {
    }

    Shipment shipment(ShipmentState state) {
        return Shipment.builder()
                .id(1000L)
                .drone(new Drone())
                .shipmentState(state)
                .build();
    }

    Drone drone(DroneState state, Double battery) {
        return Drone.builder()
                .id(100L)
                .state(state)
                .batteryCapacity(battery)
                .build();
    }
}