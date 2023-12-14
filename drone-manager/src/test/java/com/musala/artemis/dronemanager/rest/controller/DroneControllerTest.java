package com.musala.artemis.dronemanager.rest.controller;

import com.musala.artemis.dronemanager.model.Drone;
import com.musala.artemis.dronemanager.model.DroneModel;
import com.musala.artemis.dronemanager.model.DroneState;
import com.musala.artemis.dronemanager.service.DroneService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DroneController.class)
class DroneControllerTest {
    @MockBean
    DroneService droneService;

    @Autowired
    MockMvc mockMvc;


    @Test
    void getAllDrones() throws Exception {
        Mockito.when(droneService.findAllDrones(null)).thenReturn(droneList());

        mockMvc.perform(get("/rest/v1/drone"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id", Matchers.is(1)))
                .andExpect(jsonPath("$[1].id", Matchers.is(2)))
                .andExpect(jsonPath("$[2].id", Matchers.is(3)));
    }

    @Test
    void createDrone() throws Exception {
        Mockito.when(droneService.addDrone(any())).thenReturn(drone());

        RequestBuilder request = post("/rest/v1/drone")
                .contentType(MediaType.APPLICATION_JSON)
                .content(DroneControllerTest.class.getClassLoader().getResourceAsStream("CreateDrone.json").readAllBytes());

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", Matchers.notNullValue()));
    }

    @Test
    void getDrone() throws Exception {
        Mockito.when(droneService.findDrone(any())).thenReturn(drone());

        mockMvc.perform(get("/rest/v1/drone/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.notNullValue()));
    }

    @Test
    void updateDrone() throws Exception {
        Mockito.when(droneService.patchDrone(any(), any())).thenReturn(drone());

        RequestBuilder request = patch("/rest/v1/drone/{id}", 1)
                .contentType("application/json-patch+json")
                .content(DroneControllerTest.class.getClassLoader().getResourceAsStream("DronePatch.json").readAllBytes());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.notNullValue()));
    }

    @Test
    void updateDrone_unsupportedMediaType() throws Exception {
        Mockito.when(droneService.patchDrone(any(), any())).thenReturn(drone());

        RequestBuilder request = patch("/rest/v1/drone/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(DroneControllerTest.class.getClassLoader().getResourceAsStream("DronePatch.json").readAllBytes());

        mockMvc.perform(request)
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void deleteDrone() throws Exception {
        mockMvc.perform(delete("/rest/v1/drone/{id}", 1))
                .andExpect(status().isNoContent());
    }

    Drone drone() {
        return new Drone(1L, "asd1", droneModel(), 40.0, 50.0, DroneState.IDLE);
    }

    List<Drone> droneList() {
        List<Drone> res = new ArrayList<>();
        res.add(new Drone(1L, "asd1", droneModel(), null, null, null));
        res.add(new Drone(2L, "asd2", droneModel(), null, null, null));
        res.add(new Drone(3L, "asd3", droneModel(), null, null, null));
        return res;
    }

    DroneModel droneModel() {
        DroneModel res = new DroneModel();
        res.setId(10L);
        res.setName("Lightweight");
        res.setDefaultWeightLimit(50.0);
        return res;
    }
}