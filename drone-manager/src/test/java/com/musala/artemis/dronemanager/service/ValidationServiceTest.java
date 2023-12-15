package com.musala.artemis.dronemanager.service;

import com.musala.artemis.dronemanager.DoubleListConverter;
import com.musala.artemis.dronemanager.exception.business.FleetOverflowException;
import com.musala.artemis.dronemanager.exception.business.NotApplicableForStateException;
import com.musala.artemis.dronemanager.model.Drone;
import com.musala.artemis.dronemanager.model.DroneState;
import com.musala.artemis.dronemanager.model.Medication;
import com.musala.artemis.dronemanager.model.Shipment;
import com.musala.artemis.dronemanager.model.ShipmentState;
import jakarta.annotation.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValidationServiceTest {
    ValidationService validationService;

    @BeforeEach
    void setUp() {
        validationService = new ValidationService(25.0, 10L);
    }

    @ParameterizedTest
    @CsvSource({
            "1, true",
            "9, true",
            "10, false",
            "15, false",
    })
    void validateAddToFleet(Long fleetSize, boolean noException) {
        if (noException)
            assertDoesNotThrow(() -> validationService.validateAddToFleet(fleetSize));
        else
            assertThrowsExactly(FleetOverflowException.class, () -> validationService.validateAddToFleet(fleetSize));
    }

    @ParameterizedTest
    @CsvSource({
            "NEW, true",
            "LOADING, true",
            "LOADED, true",
            "DELIVERING, false",
            "CANCELLED_RETURNING, false",
            "DELIVERED, false",
            "CANCELLED, false",
    })
    void validateUpdate(ShipmentState state, boolean noException) {
        if (noException)
            assertDoesNotThrow(() -> validationService.validateUpdate(shipment(state)));
        else
            assertThrowsExactly(NotApplicableForStateException.class, () -> validationService.validateUpdate(shipment(state)));
    }

    @ParameterizedTest
    @CsvSource({
            "NEW, IDLE, 100, ",
            "NEW, IDLE, 50, ",
            "NEW, IDLE, 25, ",
            "NEW, IDLE, 10, com.musala.artemis.dronemanager.exception.business.LowBatteryLevelException",
            "NEW, IDLE, 24.9, com.musala.artemis.dronemanager.exception.business.LowBatteryLevelException",
            "LOADING, IDLE, 100, com.musala.artemis.dronemanager.exception.business.NotApplicableForStateException",
            "DELIVERING, IDLE, 100, com.musala.artemis.dronemanager.exception.business.NotApplicableForStateException",
            "CANCELLED, IDLE, 100, com.musala.artemis.dronemanager.exception.business.NotApplicableForStateException",
            "NEW, LOADING, 100, com.musala.artemis.dronemanager.exception.business.NotApplicableForStateException",
            "NEW, DELIVERING, 100, com.musala.artemis.dronemanager.exception.business.NotApplicableForStateException",
    })
    <T extends Throwable> void validateStartLoading(ShipmentState shipmentState, DroneState droneState, Double battery,
                                                    @Nullable Class<T> tClass) {
        Drone drone = drone(droneState);
        drone.setBatteryCapacity(battery);
        if (tClass == null)
            assertDoesNotThrow(() -> validationService.validateStartLoading(shipment(shipmentState), drone));
        else
            assertThrowsExactly(tClass, () -> validationService.validateStartLoading(shipment(shipmentState), drone));
    }

    @ParameterizedTest
    @CsvSource({
            "LOADING, LOADING, true",
            "LOADING, IDLE, false",
            "LOADING, DELIVERING, false",
            "LOADING, RETURNING, false",
            "NEW, LOADING, false",
            "DELIVERING, LOADING, false",
            "CANCELLED, LOADING, false",
            "CANCELLED, RETURNING, false",
    })
    void validateLoading(ShipmentState shipmentState, DroneState droneState, boolean noException) {
        if (noException)
            assertDoesNotThrow(() -> validationService.validateLoading(shipment(shipmentState), drone(droneState)));
        else
            assertThrowsExactly(NotApplicableForStateException.class, () -> validationService.validateLoading(shipment(shipmentState), drone(droneState)));
    }

    @ParameterizedTest
    @CsvSource({
            "LOADING, LOADING, 50, 10:20, ",
            "LOADING, LOADING, 10, 5:4, ",
            "LOADING, LOADING, 100, 20:20:20:20:20, ",
            "LOADING, LOADING, 10, 10, ",
            "NEW, LOADING, 50, 10:20, com.musala.artemis.dronemanager.exception.business.NotApplicableForStateException",
            "DELIVERING, LOADING, 50, 10:20, com.musala.artemis.dronemanager.exception.business.NotApplicableForStateException",
            "LOADING, IDLE, 50, 10:20, com.musala.artemis.dronemanager.exception.business.NotApplicableForStateException",
            "LOADING, RETURNING, 50, 10:20, com.musala.artemis.dronemanager.exception.business.NotApplicableForStateException",
            "LOADING, LOADING, 50, 10:20:30, com.musala.artemis.dronemanager.exception.business.DroneOverloadException",
            "LOADING, LOADING, 10, 10:20, com.musala.artemis.dronemanager.exception.business.DroneOverloadException",
            "LOADING, LOADING, 50, 51, com.musala.artemis.dronemanager.exception.business.DroneOverloadException",
            "LOADING, LOADING, 50, , com.musala.artemis.dronemanager.exception.business.EmptyShipmentException",
    })
    <T extends Throwable> void validateFinishLoading(ShipmentState shipmentState, DroneState droneState,
                                                     Double droneWeightLimit, @ConvertWith(DoubleListConverter.class) List<Double> weights,
                                                     @Nullable Class<T> tClass) {
        Shipment shipment = shipment(shipmentState);
        shipment.setMedications(weights.stream().map(this::medication).toList());
        Drone drone = drone(droneState);
        drone.setWeightLimit(droneWeightLimit);
        if (tClass == null)
            assertDoesNotThrow(() -> validationService.validateFinishLoading(shipment, drone));
        else
            assertThrowsExactly(tClass, () -> validationService.validateFinishLoading(shipment, drone));
    }

    @ParameterizedTest
    @CsvSource({
            "DELIVERING, DELIVERING, true",
            "DELIVERING, IDLE, false",
            "DELIVERING, DELIVERED, false",
            "NEW, DELIVERING, false",
            "CANCELLED, DELIVERING, false",
    })
    void validateFinishDelivery(ShipmentState shipmentState, DroneState droneState, boolean noException) {
        if (noException)
            assertDoesNotThrow(() -> validationService.validateFinishDelivery(shipment(shipmentState), drone(droneState)));
        else
            assertThrowsExactly(NotApplicableForStateException.class, () -> validationService.validateFinishDelivery(shipment(shipmentState), drone(droneState)));
    }

    @ParameterizedTest
    @CsvSource({
            "LOADED, LOADED, john doe, ",
            "LOADED, LOADED, asdf, ",
            "NEW, LOADED, john doe, com.musala.artemis.dronemanager.exception.business.NotApplicableForStateException",
            "DELIVERING, LOADED, john doe, com.musala.artemis.dronemanager.exception.business.NotApplicableForStateException",
            "LOADED, IDLE, john doe, com.musala.artemis.dronemanager.exception.business.NotApplicableForStateException",
            "LOADED, RETURNING, john doe, com.musala.artemis.dronemanager.exception.business.NotApplicableForStateException",
            "LOADED, LOADED, , com.musala.artemis.dronemanager.exception.business.BlankAddresseeException",
    })
    <T extends Throwable> void validateStartDelivery(ShipmentState shipmentState, DroneState droneState,
                                                     String shipmentAddressee, @Nullable Class<T> tClass) {
        Shipment shipment = shipment(shipmentState);
        shipment.setAddressee(shipmentAddressee);
        if (tClass == null)
            assertDoesNotThrow(() -> validationService.validateStartDelivery(shipment, drone(droneState)));
        else
            assertThrowsExactly(tClass, () -> validationService.validateStartDelivery(shipment, drone(droneState)));
    }

    @ParameterizedTest
    @CsvSource({
            "NEW, IDLE, true",
            "NEW, LOADING, true",
            "NEW, DELIVERING, true",
            "LOADING, IDLE, true",
            "LOADED, IDLE, true",
            "DELIVERING, IDLE, true",
            "CANCELLED_RETURNING, IDLE, false",
            "DELIVERING, DELIVERED, false",
            "CANCELLED, LOADING, false",
            "NEW, RETURNING, false",
    })
    void validateCancel(ShipmentState shipmentState, DroneState droneState, boolean noException) {
        if (noException)
            assertDoesNotThrow(() -> validationService.validateCancel(shipment(shipmentState), drone(droneState)));
        else
            assertThrowsExactly(NotApplicableForStateException.class, () -> validationService.validateCancel(shipment(shipmentState), drone(droneState)));
    }

    @ParameterizedTest
    @CsvSource({
            "CANCELLED_RETURNING, DELIVERING, true",
            "CANCELLED_RETURNING, DELIVERED, true",
            " , DELIVERED, true",
            "NEW, DELIVERING, false",
            "LOADING, IDLE, false",
            "LOADED, IDLE, false",
            "DELIVERING, IDLE, false",
            "CANCELLED_RETURNING, IDLE, false",
            "DELIVERING, DELIVERED, false",
            "CANCELLED, LOADING, false",
            "NEW, RETURNING, false",
            " , IDLE, false",
            " , RETURNING, false",
    })
    void validateDroneReturn(@Nullable ShipmentState shipmentState, DroneState droneState, boolean noException) {
        Shipment shipment = shipmentState != null ? shipment(shipmentState) : null;
        if (noException)
            assertDoesNotThrow(() -> validationService.validateDroneReturn(shipment, drone(droneState)));
        else
            assertThrowsExactly(NotApplicableForStateException.class, () -> validationService.validateDroneReturn(shipment, drone(droneState)));
    }

    @ParameterizedTest
    @CsvSource({
            " , RETURNING, true",
            "CANCELLED_RETURNING, RETURNING, true",
            "NEW, DELIVERING, false",
            "LOADING, IDLE, false",
            "LOADED, IDLE, false",
            "DELIVERING, IDLE, false",
            "CANCELLED_RETURNING, IDLE, false",
            "DELIVERING, DELIVERED, false",
            "CANCELLED, LOADING, false",
            "NEW, RETURNING, false",
            " , IDLE, false",
            " , DELIVERED, false",
    })
    void validateDroneFinishReturn(@Nullable ShipmentState shipmentState, DroneState droneState, boolean noException) {
        Shipment shipment = shipmentState != null ? shipment(shipmentState) : null;
        if (noException)
            assertDoesNotThrow(() -> validationService.validateDroneFinishReturn(shipment, drone(droneState)));
        else
            assertThrowsExactly(NotApplicableForStateException.class, () -> validationService.validateDroneFinishReturn(shipment, drone(droneState)));
    }

    Shipment shipment(ShipmentState state) {
        return Shipment.builder()
                .id(1000L)
                .drone(new Drone())
                .shipmentState(state)
                .build();
    }

    Drone drone(DroneState state) {
        return Drone.builder()
                .id(100L)
                .state(state)
                .build();
    }

    Medication medication(Double weight) {
        return Medication.builder()
                .id(500L)
                .weight(weight)
                .build();
    }
}