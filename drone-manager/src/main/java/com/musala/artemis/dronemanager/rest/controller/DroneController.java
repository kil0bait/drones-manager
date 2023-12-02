package com.musala.artemis.dronemanager.rest.controller;

import com.github.fge.jsonpatch.JsonPatch;
import com.musala.artemis.dronemanager.exception.NonUniqueException;
import com.musala.artemis.dronemanager.exception.PrimaryNotFoundException;
import com.musala.artemis.dronemanager.exception.RelationNotFoundException;
import com.musala.artemis.dronemanager.model.Drone;
import com.musala.artemis.dronemanager.rest.annotation.CreatedApiResponses;
import com.musala.artemis.dronemanager.rest.annotation.DeleteApiResponses;
import com.musala.artemis.dronemanager.rest.annotation.FindByIdApiResponses;
import com.musala.artemis.dronemanager.rest.model.CreateDroneRequest;
import com.musala.artemis.dronemanager.rest.model.DroneResponse;
import com.musala.artemis.dronemanager.service.DroneManagerService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rest/v1/drone")
@RequiredArgsConstructor
public class DroneController {
    private final DroneManagerService droneManagerService;

    @Operation(summary = "Get all drones",
            description = "Get all drones")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DroneResponse>> getAllDrones() {
        List<DroneResponse> list = droneManagerService.findAllDrones().stream().map(DroneResponse::new).toList();
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Create drone",
            description = "Create drone")
    @CreatedApiResponses
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DroneResponse> createDrone(@RequestBody @Validated CreateDroneRequest createDroneRequest)
            throws RelationNotFoundException, NonUniqueException {
        Drone drone = droneManagerService.addDrone(createDroneRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(new DroneResponse(drone));
    }

    @Operation(summary = "Get drone",
            description = "Get drone by id")
    @FindByIdApiResponses
    @GetMapping(value = "/{droneId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DroneResponse> getDrone(@PathVariable Long droneId) throws PrimaryNotFoundException {
        Drone drone = droneManagerService.findDrone(droneId);
        return ResponseEntity.ok(new DroneResponse(drone));
    }

    @Operation(summary = "Update drone",
            description = "Update drone parameters by id")
    @FindByIdApiResponses
    @PatchMapping(value = "/{droneId}", consumes = "application/json-patch+json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DroneResponse> updateDrone(@RequestBody @Validated JsonPatch jsonPatch, @PathVariable Long droneId) throws PrimaryNotFoundException {
        Drone drone = droneManagerService.findDrone(droneId);
        return ResponseEntity.ok(new DroneResponse(drone));
    }

    @Operation(summary = "Delete drone",
            description = "Delete drone by id")
    @DeleteApiResponses
    @DeleteMapping(value = "/{droneId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteDrone(@PathVariable Long droneId) throws PrimaryNotFoundException {
        droneManagerService.deleteDrone(droneId);
        return ResponseEntity.noContent().build();
    }

}
