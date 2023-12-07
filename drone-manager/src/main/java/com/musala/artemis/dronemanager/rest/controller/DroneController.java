package com.musala.artemis.dronemanager.rest.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.musala.artemis.dronemanager.model.Drone;
import com.musala.artemis.dronemanager.rest.annotation.CreatedApiResponses;
import com.musala.artemis.dronemanager.rest.annotation.DeleteApiResponses;
import com.musala.artemis.dronemanager.rest.annotation.FindByIdApiResponses;
import com.musala.artemis.dronemanager.rest.annotation.UpdateApiResponses;
import com.musala.artemis.dronemanager.rest.model.CreateDroneRequest;
import com.musala.artemis.dronemanager.rest.model.DroneResponse;
import com.musala.artemis.dronemanager.rest.model.PatchDocument;
import com.musala.artemis.dronemanager.service.DroneService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Drone CRUD")
@RequiredArgsConstructor
public class DroneController {
    private final DroneService droneService;

    @Operation(summary = "Get all drones",
            description = "Get all drones. Standard CRUD Read-All operation")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DroneResponse>> getAllDrones() {
        List<DroneResponse> list = droneService.findAllDrones().stream().map(DroneResponse::new).toList();
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Create drone",
            description = "Create drone. Standard CRUD Create operation")
    @CreatedApiResponses
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DroneResponse> createDrone(@RequestBody @Validated CreateDroneRequest createDroneRequest) {
        Drone drone = droneService.addDrone(createDroneRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(new DroneResponse(drone));
    }

    @Operation(summary = "Get drone",
            description = "Get drone by id. Standard CRUD Read operation")
    @FindByIdApiResponses
    @GetMapping(value = "/{droneId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DroneResponse> getDrone(@PathVariable Long droneId) {
        Drone drone = droneService.findDrone(droneId);
        return ResponseEntity.ok(new DroneResponse(drone));
    }

    @Operation(summary = "Update drone",
            description = "Update drone parameters by id. Standard CRUD Update operation",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PatchDocument.class)))))
    @UpdateApiResponses
    @PatchMapping(value = "/{droneId}", consumes = "application/json-patch+json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DroneResponse> updateDrone(@PathVariable Long droneId, @RequestBody @Validated JsonPatch jsonPatch)
            throws JsonPatchException, JsonProcessingException {
        Drone drone = droneService.patchDrone(droneId, jsonPatch);
        return ResponseEntity.ok(new DroneResponse(drone));
    }

    @Operation(summary = "Delete drone",
            description = "Delete drone by id. Standard CRUD Delete operation")
    @DeleteApiResponses
    @DeleteMapping(value = "/{droneId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteDrone(@PathVariable Long droneId) {
        droneService.deleteDrone(droneId);
        return ResponseEntity.noContent().build();
    }

}
