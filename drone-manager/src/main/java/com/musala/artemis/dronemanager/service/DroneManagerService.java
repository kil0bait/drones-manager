package com.musala.artemis.dronemanager.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.musala.artemis.dronemanager.dao.DroneModelRepository;
import com.musala.artemis.dronemanager.dao.DroneRepository;
import com.musala.artemis.dronemanager.exception.NonUniqueException;
import com.musala.artemis.dronemanager.exception.PatchValidationException;
import com.musala.artemis.dronemanager.exception.PrimaryNotFoundException;
import com.musala.artemis.dronemanager.exception.RelationNotFoundException;
import com.musala.artemis.dronemanager.exception.business.NotValidWeightLimitException;
import com.musala.artemis.dronemanager.model.Drone;
import com.musala.artemis.dronemanager.model.DroneModel;
import com.musala.artemis.dronemanager.model.DroneState;
import com.musala.artemis.dronemanager.rest.model.CreateDroneRequest;
import com.musala.artemis.dronemanager.rest.model.patch.DronePatch;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DroneManagerService {
    private static final Double INITIAL_BATTERY_CAPACITY = 100.0;

    private final DroneRepository droneRepository;
    private final DroneModelRepository droneModelRepository;
    private final ValidationService validationService;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    public List<Drone> findAllDrones() {
        return droneRepository.findAll();
    }

    public Drone addDrone(CreateDroneRequest createDroneRequest) {
        validationService.validateAddToFleet(droneRepository.count());
        DroneModel model = droneModelRepository.findByName(createDroneRequest.getModel())
                .orElseThrow(() -> new RelationNotFoundException(DroneModel.class, "name", createDroneRequest.getModel()));
        Optional<Drone> existing = droneRepository.findBySerialNumber(createDroneRequest.getSerialNumber());
        if (existing.isPresent())
            throw new NonUniqueException(Drone.class, "serialNumber", createDroneRequest.getSerialNumber());
        Drone drone = Drone.builder().serialNumber(createDroneRequest.getSerialNumber())
                .model(model)
                .state(DroneState.IDLE)
                .weightLimit(validateAndGetWeightLimit(createDroneRequest.getWeightLimit(), model))
                .batteryCapacity(Optional.ofNullable(createDroneRequest.getBatteryCapacity()).orElse(INITIAL_BATTERY_CAPACITY))
                .build();
        return droneRepository.save(drone);
    }

    public Drone findDrone(Long droneId) {
        return droneRepository.findById(droneId)
                .orElseThrow(() -> new PrimaryNotFoundException(Drone.class, "id", String.valueOf(droneId)));
    }

    public Drone patchDrone(Long droneId, JsonPatch jsonPatch) throws JsonPatchException, JsonProcessingException {
        Drone drone = findDrone(droneId);
        DronePatch dronePatch = new DronePatch(drone);
        JsonNode patched = jsonPatch.apply(objectMapper.convertValue(dronePatch, JsonNode.class));
        DronePatch patchedModel = objectMapper.treeToValue(patched, DronePatch.class);
        Errors errors = validator.validateObject(patchedModel);
        if (errors.hasErrors())
            throw new PatchValidationException(errors);
        applyChanges(drone, patchedModel);
        validateAndGetWeightLimit(drone.getWeightLimit(), drone.getModel());
        return updateDrone(drone);
    }

    public Drone updateDrone(Drone drone) {
        return droneRepository.save(drone);
    }

    public void deleteDrone(Long droneId) {
        Drone drone = findDrone(droneId);
        droneRepository.delete(drone);
    }

    private void applyChanges(Drone orig, DronePatch patched) {
        DroneModel model = droneModelRepository.findByName(patched.getModel())
                .orElseThrow(() -> new RelationNotFoundException(DroneModel.class, "name", patched.getModel()));
        orig.setModel(model);
        orig.setWeightLimit(patched.getWeightLimit());
        orig.setBatteryCapacity(patched.getBatteryCapacity());
    }

    private static Double validateAndGetWeightLimit(@Nullable Double weightLimit, DroneModel model) {
        if (weightLimit == null)
            return model.getDefaultWeightLimit();
        if (weightLimit > model.getDefaultWeightLimit())
            throw new NotValidWeightLimitException(model);
        return weightLimit;
    }

}
