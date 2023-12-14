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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.SimpleErrors;
import org.springframework.validation.Validator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DroneServiceTest {
    @InjectMocks
    DroneService droneService;

    @Mock
    DroneRepository droneRepository;
    @Mock
    DroneModelRepository droneModelRepository;
    @Mock
    ValidationService validationService;
    @Mock
    ObjectMapper objectMapper;
    @Mock
    Validator validator;

    static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAllDrones_success() {
        when(droneRepository.findAll()).thenReturn(droneList());

        List<Drone> allDrones = droneService.findAllDrones(null);

        assertEquals(3, allDrones.size());
        assertTrue(allDrones.stream().anyMatch(drone -> drone.getId() == 1L));
        assertTrue(allDrones.stream().anyMatch(drone -> drone.getId() == 2L));
        assertTrue(allDrones.stream().anyMatch(drone -> drone.getId() == 3L));
    }

    @Test
    void findAllDrones_success2() {
        when(droneRepository.findByState(any())).thenReturn(droneList());

        List<Drone> allDrones = droneService.findAllDrones(DroneState.IDLE);

        assertEquals(3, allDrones.size());
        assertTrue(allDrones.stream().anyMatch(drone -> drone.getId() == 1L));
        assertTrue(allDrones.stream().anyMatch(drone -> drone.getId() == 2L));
        assertTrue(allDrones.stream().anyMatch(drone -> drone.getId() == 3L));
    }

    @Test
    void findDrone_success() {
        when(droneRepository.findById(anyLong())).thenReturn(Optional.of(drone()));

        Drone drone = droneService.findDrone(1L);

        assertNotNull(drone);
    }

    @Test
    void findDrone_notFound() {
        when(droneRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrowsExactly(PrimaryNotFoundException.class, () -> droneService.findDrone(100L));
    }

    @Test
    void addDrone_success() {
        doNothing().when(validationService).validateAddToFleet(anyLong());
        when(droneModelRepository.findByName(any())).thenReturn(Optional.of(droneModel()));
        when(droneRepository.findBySerialNumber(any())).thenReturn(Optional.empty());
        when(droneRepository.save(any())).thenReturn(drone());

        Drone drone = droneService.addDrone(createDroneRequest());

        assertNotNull(drone);
    }

    @Test
    void addDrone_success2() {
        doNothing().when(validationService).validateAddToFleet(anyLong());
        when(droneModelRepository.findByName(any())).thenReturn(Optional.of(droneModel()));
        when(droneRepository.findBySerialNumber(any())).thenReturn(Optional.empty());
        when(droneRepository.save(any())).thenReturn(drone());
        CreateDroneRequest createDroneRequest = createDroneRequest();
        createDroneRequest.setWeightLimit(null);

        Drone drone = droneService.addDrone(createDroneRequest);

        assertNotNull(drone);
    }

    @Test
    void addDrone_exceptionDroneModelNotFound() {
        doNothing().when(validationService).validateAddToFleet(anyLong());
        when(droneModelRepository.findByName(any())).thenReturn(Optional.empty());
        when(droneRepository.findBySerialNumber(any())).thenReturn(Optional.empty());
        when(droneRepository.save(any())).thenReturn(drone());

        assertThrowsExactly(RelationNotFoundException.class, () -> droneService.addDrone(createDroneRequest()));
    }

    @Test
    void addDrone_exceptionSerialNumberIsNotUnique() {
        doNothing().when(validationService).validateAddToFleet(anyLong());
        when(droneModelRepository.findByName(any())).thenReturn(Optional.of(droneModel()));
        when(droneRepository.findBySerialNumber(any())).thenReturn(Optional.of(new Drone()));
        when(droneRepository.save(any())).thenReturn(drone());

        assertThrowsExactly(NonUniqueException.class, () -> droneService.addDrone(createDroneRequest()));
    }

    @Test
    void addDrone_exceptionNotValidWeight() {
        doNothing().when(validationService).validateAddToFleet(anyLong());
        when(droneModelRepository.findByName(any())).thenReturn(Optional.of(droneModel()));
        when(droneRepository.findBySerialNumber(any())).thenReturn(Optional.empty());
        when(droneRepository.save(any())).thenReturn(drone());
        CreateDroneRequest createDroneRequest = createDroneRequest();
        createDroneRequest.setWeightLimit(100.0);

        assertThrowsExactly(NotValidWeightLimitException.class, () -> droneService.addDrone(createDroneRequest));
    }

    @Test
    void patchDrone_success() throws JsonPatchException, JsonProcessingException {
        when(droneRepository.findById(anyLong())).thenReturn(Optional.of(drone()));
        when(objectMapper.convertValue(any(), any(Class.class))).thenReturn(jsonNodeDronePatch());
        when(objectMapper.treeToValue(any(), any(Class.class))).thenReturn(new DronePatch(drone()));
        when(droneModelRepository.findByName(anyString())).thenReturn(Optional.of(droneModel()));
        when(validator.validateObject(any())).thenReturn(new SimpleErrors(new Object()));
        when(droneRepository.save(any())).thenReturn(drone());

        Drone drone = droneService.patchDrone(123L, dronePatch());

        assertNotNull(drone);
    }

    @Test
    void patchDrone_exceptionNotFound() throws JsonPatchException, JsonProcessingException {
        when(droneRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(objectMapper.convertValue(any(), any(Class.class))).thenReturn(jsonNodeDronePatch());
        when(objectMapper.treeToValue(any(), any(Class.class))).thenReturn(new DronePatch(drone()));
        when(droneModelRepository.findByName(anyString())).thenReturn(Optional.of(droneModel()));
        when(validator.validateObject(any())).thenReturn(new SimpleErrors(new Object()));

        assertThrowsExactly(PrimaryNotFoundException.class, () -> droneService.patchDrone(123L, dronePatch()));
    }

    @Test
    void patchDrone_exceptionDroneModelNotFound() throws JsonPatchException, JsonProcessingException {
        when(droneRepository.findById(anyLong())).thenReturn(Optional.of(drone()));
        when(objectMapper.convertValue(any(), any(Class.class))).thenReturn(jsonNodeDronePatch());
        when(objectMapper.treeToValue(any(), any(Class.class))).thenReturn(new DronePatch(drone()));
        when(droneModelRepository.findByName(anyString())).thenReturn(Optional.empty());
        when(validator.validateObject(any())).thenReturn(new SimpleErrors(new Object()));

        assertThrowsExactly(RelationNotFoundException.class, () -> droneService.patchDrone(123L, dronePatch()));
    }

    @Test
    void patchDrone_exceptionValidation() throws JsonPatchException, JsonProcessingException {
        when(droneRepository.findById(anyLong())).thenReturn(Optional.of(drone()));
        when(objectMapper.convertValue(any(), any(Class.class))).thenReturn(jsonNodeDronePatch());
        when(objectMapper.treeToValue(any(), any(Class.class))).thenReturn(new DronePatch(drone()));
        when(droneModelRepository.findByName(anyString())).thenReturn(Optional.of(droneModel()));
        SimpleErrors errors = new SimpleErrors(new Object());
        errors.reject("test");
        when(validator.validateObject(any())).thenReturn(errors);

        assertThrowsExactly(PatchValidationException.class, () -> droneService.patchDrone(123L, dronePatch()));
    }
    @Test
    void patchDrone_exceptionNotValidWeight() throws JsonPatchException, JsonProcessingException {
        when(droneRepository.findById(anyLong())).thenReturn(Optional.of(drone()));
        when(objectMapper.convertValue(any(), any(Class.class))).thenReturn(jsonNodeDronePatch());
        DronePatch dronePatch = new DronePatch(drone());
        dronePatch.setWeightLimit(1000.0);
        when(objectMapper.treeToValue(any(), any(Class.class))).thenReturn(dronePatch);
        when(droneModelRepository.findByName(anyString())).thenReturn(Optional.of(droneModel()));
        when(validator.validateObject(any())).thenReturn(new SimpleErrors(new Object()));

        assertThrowsExactly(NotValidWeightLimitException.class, () -> droneService.patchDrone(123L, dronePatch()));
    }

    @Test
    void updateDrone_success() {
        when(droneRepository.save(any())).thenReturn(drone());

        Drone drone = droneService.updateDrone(drone());

        assertNotNull(drone);
    }

    @Test
    void deleteDrone_success() {
        when(droneRepository.findById(anyLong())).thenReturn(Optional.of(drone()));

        assertDoesNotThrow(() -> droneService.deleteDrone(123L));
    }

    @Test
    void deleteDrone_exceptionNotFound() {
        when(droneRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrowsExactly(PrimaryNotFoundException.class, () -> droneService.deleteDrone(123L));
    }

    Drone drone() {
        return new Drone(1L, "asd1", droneModel(), 40.0, 50.0, DroneState.IDLE);
    }

    List<Drone> droneList() {
        List<Drone> res = new ArrayList<>();
        res.add(new Drone(1L, "asd1", null, null, null, null));
        res.add(new Drone(2L, "asd2", null, null, null, null));
        res.add(new Drone(3L, "asd3", null, null, null, null));
        return res;
    }

    CreateDroneRequest createDroneRequest() {
        CreateDroneRequest res = new CreateDroneRequest();
        res.setSerialNumber("ASDA1233");
        res.setWeightLimit(10.0);
        res.setModel("Lightweight");
        res.setBatteryCapacity(50.0);
        return res;
    }

    DroneModel droneModel() {
        DroneModel res = new DroneModel();
        res.setId(10L);
        res.setName("Lightweight");
        res.setDefaultWeightLimit(50.0);
        return res;
    }

    JsonPatch dronePatch() {
        try {
            return JsonPatch.fromJson(OBJECT_MAPPER.readTree(
                    DroneServiceTest.class.getClassLoader().getResourceAsStream("DronePatch.json")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    JsonNode jsonNodeDronePatch() {
        return OBJECT_MAPPER.convertValue(new DronePatch(drone()), JsonNode.class);
    }

}