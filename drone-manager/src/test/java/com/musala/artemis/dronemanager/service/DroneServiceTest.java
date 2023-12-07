package com.musala.artemis.dronemanager.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musala.artemis.dronemanager.dao.DroneModelRepository;
import com.musala.artemis.dronemanager.dao.DroneRepository;
import com.musala.artemis.dronemanager.exception.PrimaryNotFoundException;
import com.musala.artemis.dronemanager.model.Drone;
import com.musala.artemis.dronemanager.model.DroneModel;
import com.musala.artemis.dronemanager.rest.model.CreateDroneRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.Validator;

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

    List<Drone> dronesRepoList;
    Drone drone1;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        dronesRepoList = new ArrayList<>();
        dronesRepoList.add(drone1 = new Drone(1L, null, null, null, null, null));
        dronesRepoList.add(new Drone(2L, null, null, null, null, null));
        dronesRepoList.add(new Drone(3L, null, null, null, null, null));
    }

    @Test
    void findAllDrones_success() {
        when(droneRepository.findAll()).thenReturn(dronesRepoList);

        List<Drone> allDrones = droneService.findAllDrones();

        assertEquals(3, allDrones.size());
        assertTrue(allDrones.stream().anyMatch(drone -> drone.getId() == 1L));
        assertTrue(allDrones.stream().anyMatch(drone -> drone.getId() == 2L));
        assertTrue(allDrones.stream().anyMatch(drone -> drone.getId() == 3L));
    }

    @Test
    void findDrone_success() {
        when(droneRepository.findById(anyLong())).thenReturn(Optional.of(drone1));

        Drone drone = droneService.findDrone(1L);

        assertNotNull(drone);
    }

    @Test
    void findDrone_notFound() {
        when(droneRepository.findById(anyLong())).thenReturn(Optional.empty());

        PrimaryNotFoundException e = assertThrowsExactly(PrimaryNotFoundException.class, () -> droneService.findDrone(100L));
        assertTrue(e.getMessage().contains("100"));
    }

    @Test
    void addDrone_success() {
        doNothing().when(validationService).validateAddToFleet(anyLong());
        when(droneModelRepository.findByName(any())).thenReturn(Optional.of(new DroneModel()));
        when(droneRepository.findBySerialNumber(anyString())).thenReturn(Optional.empty());
        when(droneRepository.save(any())).thenReturn(drone1);

        Drone drone = droneService.addDrone(new CreateDroneRequest());

        assertNotNull(drone);
    }


    @Test
    void patchDrone() {
    }

    @Test
    void updateDrone() {
    }

    @Test
    void deleteDrone() {
    }
}