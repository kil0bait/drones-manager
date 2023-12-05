package com.musala.artemis.dronemanager.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.musala.artemis.dronemanager.dao.DroneRepository;
import com.musala.artemis.dronemanager.dao.ShipmentRepository;
import com.musala.artemis.dronemanager.exception.PatchValidationException;
import com.musala.artemis.dronemanager.exception.PrimaryNotFoundException;
import com.musala.artemis.dronemanager.exception.RelationNotFoundException;
import com.musala.artemis.dronemanager.exception.business.ExistingShipmentException;
import com.musala.artemis.dronemanager.model.Drone;
import com.musala.artemis.dronemanager.model.Shipment;
import com.musala.artemis.dronemanager.model.ShipmentState;
import com.musala.artemis.dronemanager.rest.model.CreateShipmentRequest;
import com.musala.artemis.dronemanager.rest.model.patch.ShipmentPatch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShipmentService {
    private final ShipmentRepository shipmentRepository;
    private final DroneRepository droneRepository;
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
}
