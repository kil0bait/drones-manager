package com.musala.artemis.dronemanager.rest.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.musala.artemis.dronemanager.model.Shipment;
import com.musala.artemis.dronemanager.rest.annotation.CreatedApiResponses;
import com.musala.artemis.dronemanager.rest.annotation.DeleteApiResponses;
import com.musala.artemis.dronemanager.rest.annotation.FindByIdApiResponses;
import com.musala.artemis.dronemanager.rest.annotation.PatchApiResponses;
import com.musala.artemis.dronemanager.rest.model.CreateShipmentRequest;
import com.musala.artemis.dronemanager.rest.model.PatchDocument;
import com.musala.artemis.dronemanager.rest.model.ShipmentResponse;
import com.musala.artemis.dronemanager.service.ShipmentService;
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
@RequestMapping("/rest/v1/shipment")
@Tag(name = "Shipment CRUD")
@RequiredArgsConstructor
public class ShipmentController {
    private final ShipmentService shipmentService;

    @Operation(summary = "Get all shipments",
            description = "Get all shipments. Standard CRUD Read-All operation")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ShipmentResponse>> getAllShipments() {
        List<ShipmentResponse> list = shipmentService.findAllShipments().stream().map(ShipmentResponse::new).toList();
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Create shipment",
            description = "Create shipment. Standard CRUD Create operation")
    @CreatedApiResponses
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ShipmentResponse> createShipment(@RequestBody @Validated CreateShipmentRequest createShipmentRequest) {
        Shipment shipment = shipmentService.addShipment(createShipmentRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ShipmentResponse(shipment));
    }

    @Operation(summary = "Get shipment",
            description = "Get shipment by id. Standard CRUD Read operation")
    @FindByIdApiResponses
    @GetMapping(value = "/{shipmentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ShipmentResponse> getShipment(@PathVariable Long shipmentId) {
        Shipment shipment = shipmentService.findShipment(shipmentId);
        return ResponseEntity.ok(new ShipmentResponse(shipment));
    }

    @Operation(summary = "Update shipment",
            description = "Update shipment parameters by id. Standard CRUD Update operation",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PatchDocument.class)))))
    @PatchApiResponses
    @PatchMapping(value = "/{shipmentId}", consumes = "application/json-patch+json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ShipmentResponse> updateShipment(@PathVariable Long shipmentId, @RequestBody @Validated JsonPatch jsonPatch)
            throws JsonPatchException, JsonProcessingException {
        Shipment shipment = shipmentService.patchShipment(shipmentId, jsonPatch);
        return ResponseEntity.ok(new ShipmentResponse(shipment));
    }

    @Operation(summary = "Delete shipment",
            description = "Delete shipment by id. Standard CRUD Delete operation")
    @DeleteApiResponses
    @DeleteMapping(value = "/{shipmentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteShipment(@PathVariable Long shipmentId) {
        shipmentService.deleteShipment(shipmentId);
        return ResponseEntity.noContent().build();
    }

}
