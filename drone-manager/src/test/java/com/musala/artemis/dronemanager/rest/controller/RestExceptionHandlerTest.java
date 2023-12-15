package com.musala.artemis.dronemanager.rest.controller;

import com.github.fge.jsonpatch.JsonPatchException;
import com.musala.artemis.dronemanager.exception.PatchValidationException;
import com.musala.artemis.dronemanager.service.DroneService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.validation.Errors;

import java.util.Collections;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DroneController.class)
class RestExceptionHandlerTest {

    @MockBean
    DroneService droneService;

    @Autowired
    MockMvc mockMvc;

    @Test
    void thrownDataAccessException() throws Exception {
        Mockito.when(droneService.findAllDrones(null)).thenThrow(new DuplicateKeyException("test"));

        mockMvc.perform(get("/rest/v1/drone"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.timestamp", Matchers.notNullValue()))
                .andExpect(jsonPath("$.status", Matchers.is(500)))
                .andExpect(jsonPath("$.error", Matchers.notNullValue()))
                .andExpect(jsonPath("$.message", Matchers.notNullValue()));
    }

    @Test
    void thrownJsonPatchException() throws Exception {
        Mockito.when(droneService.patchDrone(any(), any())).thenThrow(new JsonPatchException("test"));
        RequestBuilder request = patch("/rest/v1/drone/{id}", 1)
                .contentType("application/json-patch+json")
                .content(RestExceptionHandlerTest.class.getClassLoader().getResourceAsStream("DronePatch.json").readAllBytes());

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", Matchers.notNullValue()))
                .andExpect(jsonPath("$.status", Matchers.is(400)))
                .andExpect(jsonPath("$.error", Matchers.notNullValue()))
                .andExpect(jsonPath("$.message", Matchers.notNullValue()));
    }

    @Test
    void thrownPatchValidationException() throws Exception {
        PatchValidationException mock = mock(PatchValidationException.class);
        Errors errorsMock = mock(Errors.class);
        when(mock.getErrors()).thenReturn(errorsMock);
        when(errorsMock.getAllErrors()).thenReturn(Collections.EMPTY_LIST);
        Mockito.when(droneService.findAllDrones(null)).thenThrow(mock);

        mockMvc.perform(get("/rest/v1/drone"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", Matchers.notNullValue()))
                .andExpect(jsonPath("$.status", Matchers.is(400)))
                .andExpect(jsonPath("$.error", Matchers.notNullValue()))
                .andExpect(jsonPath("$.message", Matchers.notNullValue()));
    }

}