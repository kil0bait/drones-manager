package com.musala.artemis.dronemanager.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.musala.artemis.dronemanager.dao.DroneRepository;
import com.musala.artemis.dronemanager.dao.MedicationRepository;
import com.musala.artemis.dronemanager.dao.ShipmentRepository;
import com.musala.artemis.dronemanager.exception.PatchValidationException;
import com.musala.artemis.dronemanager.exception.PrimaryNotFoundException;
import com.musala.artemis.dronemanager.exception.RelationNotFoundException;
import com.musala.artemis.dronemanager.exception.business.ExistingShipmentException;
import com.musala.artemis.dronemanager.model.Drone;
import com.musala.artemis.dronemanager.model.DroneState;
import com.musala.artemis.dronemanager.model.Medication;
import com.musala.artemis.dronemanager.model.Shipment;
import com.musala.artemis.dronemanager.model.ShipmentState;
import com.musala.artemis.dronemanager.rest.model.CreateDroneShipmentRequest;
import com.musala.artemis.dronemanager.rest.model.CreateShipmentRequest;
import com.musala.artemis.dronemanager.rest.model.patch.DroneShipmentPatch;
import com.musala.artemis.dronemanager.rest.model.patch.ShipmentPatch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShipmentService {
    private final ShipmentRepository shipmentRepository;
    private final DroneRepository droneRepository;
    private final MedicationRepository medicationRepository;
    private final ShipmentValidation shipmentValidation;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    public List<Shipment> findAllShipments() {
        return shipmentRepository.findAll();
    }

    public Shipment addShipment(CreateShipmentRequest createShipmentRequest) {
        Drone drone = droneRepository.findById(createShipmentRequest.getDroneId())
                .orElseThrow(() -> new RelationNotFoundException(Drone.class, "id", String.valueOf(createShipmentRequest.getDroneId())));
        if (shipmentRepository.findByDroneIdInActiveState(drone.getId()).isPresent())
            throw new ExistingShipmentException(drone);
        Shipment shipment = Shipment.builder()
                .drone(drone)
                .addressee(createShipmentRequest.getAddressee())
                .startDate(new Date())
                .shipmentState(ShipmentState.NEW)
                .build();
        return shipmentRepository.save(shipment);
    }

    public Shipment findShipment(Long shipmentId) {
        return shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new PrimaryNotFoundException(Shipment.class, "id", String.valueOf(shipmentId)));
    }

    public Shipment patchShipment(Long shipmentId, JsonPatch jsonPatch) throws JsonPatchException, JsonProcessingException {
        Shipment shipment = findShipment(shipmentId);
        ShipmentPatch shipmentPatch = new ShipmentPatch(shipment);
        JsonNode patched = jsonPatch.apply(objectMapper.convertValue(shipmentPatch, JsonNode.class));
        ShipmentPatch patchedModel = objectMapper.treeToValue(patched, ShipmentPatch.class);
        Errors errors = validator.validateObject(patchedModel);
        if (errors.hasErrors())
            throw new PatchValidationException(errors);
        applyChanges(shipment, patchedModel);
        return updateShipment(shipment);
    }

    public Shipment updateShipment(Shipment shipment) {
        return shipmentRepository.save(shipment);
    }

    public void deleteShipment(Long shipmentId) {
        Shipment shipment = findShipment(shipmentId);
        shipmentRepository.delete(shipment);
    }

    private void applyChanges(Shipment orig, ShipmentPatch patched) {
        orig.setAddressee(patched.getAddressee());
    }

    public Shipment findShipmentForDrone(Drone drone) {
        return shipmentRepository.findByDroneIdInActiveState(drone.getId())
                .orElseThrow(() -> new PrimaryNotFoundException(Shipment.class, "droneId", String.valueOf(drone.getId())));
    }

    public Shipment addShipmentForDrone(Drone drone, CreateDroneShipmentRequest createDroneShipmentRequest) {
        if (shipmentRepository.findByDroneIdInActiveState(drone.getId()).isPresent())
            throw new ExistingShipmentException(drone);
        Shipment shipment = Shipment.builder()
                .drone(drone)
                .addressee(createDroneShipmentRequest.getAddressee())
                .startDate(new Date())
                .shipmentState(ShipmentState.NEW)
                .build();
        return shipmentRepository.save(shipment);
    }

    public Shipment patchShipmentForDrone(Drone drone, JsonPatch jsonPatch) throws JsonPatchException, JsonProcessingException {
        Shipment shipment = findShipmentForDrone(drone);
        shipmentValidation.validateUpdate(shipment);
        DroneShipmentPatch currentShipment = new DroneShipmentPatch(shipment);
        JsonNode patched = jsonPatch.apply(objectMapper.convertValue(currentShipment, JsonNode.class));
        DroneShipmentPatch patchedShipment = objectMapper.treeToValue(patched, DroneShipmentPatch.class);
        Errors errors = validator.validateObject(patchedShipment);
        if (errors.hasErrors())
            throw new PatchValidationException(errors);
        if (detectMedicationsChanges(currentShipment, patchedShipment))
            shipmentValidation.validateLoading(shipment, drone);
        applyChanges(shipment, patchedShipment);
        return updateShipment(shipment);
    }

    private void applyChanges(Shipment orig, DroneShipmentPatch patched) {
        orig.setAddressee(patched.getAddressee());
        List<Medication> medications = new ArrayList<>();
        if (patched.getMedications() != null) {
            for (Long medicationId : patched.getMedications())
                medications.add(medicationRepository.findById(medicationId)
                        .orElseThrow(() -> new RelationNotFoundException(Medication.class, "id", String.valueOf(medicationId))));
        }
        orig.setMedications(medications);
    }

    private boolean detectMedicationsChanges(DroneShipmentPatch orig, DroneShipmentPatch patched) {
        return !orig.getMedications().equals(patched.getMedications());
    }

    public Shipment startLoading(Drone drone) {
        Shipment shipment = findShipmentForDrone(drone);
        shipmentValidation.validateStartLoading(shipment, drone);
        drone.setState(DroneState.LOADING);
        droneRepository.save(drone);
        shipment.setShipmentState(ShipmentState.LOADING);
        return shipmentRepository.save(shipment);
    }

    public Shipment finishLoading(Drone drone) {
        Shipment shipment = findShipmentForDrone(drone);
        shipmentValidation.validateFinishLoading(shipment, drone);
        drone.setState(DroneState.LOADED);
        droneRepository.save(drone);
        shipment.setShipmentState(ShipmentState.LOADED);
        return shipmentRepository.save(shipment);
    }

    public Shipment startDelivery(Drone drone) {
        Shipment shipment = findShipmentForDrone(drone);
        shipmentValidation.validateStartDelivery(shipment, drone);
        drone.setState(DroneState.DELIVERING);
        droneRepository.save(drone);
        shipment.setShipmentState(ShipmentState.DELIVERING);
        return shipmentRepository.save(shipment);
    }

    public Shipment finishDelivery(Drone drone) {
        Shipment shipment = findShipmentForDrone(drone);
        shipmentValidation.validateFinishDelivery(shipment, drone);
        drone.setState(DroneState.DELIVERED);
        droneRepository.save(drone);
        shipment.setShipmentState(ShipmentState.DELIVERED);
        shipment.setEndDate(new Date());
        return shipmentRepository.save(shipment);
    }

    public Shipment cancelShipment(Drone drone) {
        Shipment shipment = findShipmentForDrone(drone);
        shipmentValidation.validateCancel(shipment, drone);
        if (drone.getState().order >= 400)
            drone.setState(DroneState.RETURNING);
        else
            drone.setState(DroneState.IDLE);
        droneRepository.save(drone);
        if (shipment.getShipmentState() == ShipmentState.DELIVERING)
            shipment.setShipmentState(ShipmentState.CANCELLED_RETURNING);
        else {
            shipment.setShipmentState(ShipmentState.CANCELLED);
            shipment.setEndDate(new Date());
        }
        return shipmentRepository.save(shipment);
    }

    public Drone returnDrone(Drone drone) {
        Shipment shipment = shipmentRepository.findByDroneIdInActiveState(drone.getId()).orElse(null);
        shipmentValidation.validateDroneReturn(shipment, drone);
        drone.setState(DroneState.RETURNING);
        return droneRepository.save(drone);
    }

    public Drone finishReturnDrone(Drone drone) {
        Shipment shipment = shipmentRepository.findByDroneIdInActiveState(drone.getId()).orElse(null);
        shipmentValidation.validateDroneFinishReturn(shipment, drone);
        if (shipment != null && shipment.getShipmentState() == ShipmentState.CANCELLED_RETURNING) {
            shipment.setShipmentState(ShipmentState.CANCELLED);
            shipment.setEndDate(new Date());
            shipmentRepository.save(shipment);
        }
        drone.setState(DroneState.IDLE);
        return droneRepository.save(drone);
    }

}
