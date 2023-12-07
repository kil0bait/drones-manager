package com.musala.artemis.dronemanager.rest.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.musala.artemis.dronemanager.model.Drone;
import com.musala.artemis.dronemanager.model.Shipment;
import com.musala.artemis.dronemanager.rest.annotation.CreatedApiResponses;
import com.musala.artemis.dronemanager.rest.annotation.FindByIdApiResponses;
import com.musala.artemis.dronemanager.rest.annotation.UpdateApiResponses;
import com.musala.artemis.dronemanager.rest.model.CreateDroneShipmentRequest;
import com.musala.artemis.dronemanager.rest.model.DroneResponse;
import com.musala.artemis.dronemanager.rest.model.PatchDocument;
import com.musala.artemis.dronemanager.rest.model.ShipmentResponse;
import com.musala.artemis.dronemanager.service.DroneService;
import com.musala.artemis.dronemanager.service.ShipmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest/v1/drone")
@Tag(name = "Drone dispatcher")
@RequiredArgsConstructor
public class DroneDispatcherController {
    private final DroneService droneService;
    private final ShipmentService shipmentService;

    @Operation(summary = "Get active shipment for a drone",
            description = "Get active shipment for a drone")
    @FindByIdApiResponses
    @GetMapping(value = "/{droneId}/shipment", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ShipmentResponse> getDroneShipment(@PathVariable Long droneId) {
        Drone drone = droneService.findDrone(droneId);
        Shipment shipment = shipmentService.findShipmentForDrone(drone);
        return ResponseEntity.ok(new ShipmentResponse(shipment));
    }

    @Operation(summary = "Create new shipment for a drone",
            description = "Create new shipment for a drone")
    @CreatedApiResponses
    @PostMapping(value = "/{droneId}/shipment", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ShipmentResponse> createDroneShipment(@PathVariable Long droneId,
                                                                @RequestBody @Validated CreateDroneShipmentRequest createDroneShipmentRequest) {
        Drone drone = droneService.findDrone(droneId);
        Shipment shipment = shipmentService.addShipmentForDrone(drone, createDroneShipmentRequest);
        return ResponseEntity.ok(new ShipmentResponse(shipment));
    }


    @Operation(summary = "Update shipment for a drone",
            description = """
                    Update shipment for a drone. Available operations: <br/>
                    <b>/addressee</b> - add, remove, replace <br/>
                    <b>/medications</b> - add, remove, replace <br/>
                    <b>/medications/{index}</b> - add, remove, replace
                    """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PatchDocument.class)))))
    @UpdateApiResponses
    @PatchMapping(value = "/{droneId}/shipment", consumes = "application/json-patch+json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ShipmentResponse> updateShipment(@PathVariable Long droneId, @RequestBody @Validated JsonPatch jsonPatch)
            throws JsonPatchException, JsonProcessingException {
        Drone drone = droneService.findDrone(droneId);
        Shipment shipment = shipmentService.patchShipmentForDrone(drone, jsonPatch);
        return ResponseEntity.ok(new ShipmentResponse(shipment));
    }


    @Operation(summary = "Start loading shipment",
            description = "Start loading shipment for a drone")
    @UpdateApiResponses
    @PostMapping(value = "/{droneId}/shipment/load", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ShipmentResponse> startLoadDroneShipment(@PathVariable Long droneId) {
        Drone drone = droneService.findDrone(droneId);
        Shipment shipment = shipmentService.startLoading(drone);
        return ResponseEntity.ok(new ShipmentResponse(shipment));
    }

    @Operation(summary = "Finish loading shipment",
            description = "Finish loading shipment for a drone")
    @UpdateApiResponses
    @PostMapping(value = "/{droneId}/shipment/load/finish", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ShipmentResponse> finishLoadDroneShipment(@PathVariable Long droneId) {
        Drone drone = droneService.findDrone(droneId);
        Shipment shipment = shipmentService.finishLoading(drone);
        return ResponseEntity.ok(new ShipmentResponse(shipment));
    }

    @Operation(summary = "Start delivering shipment",
            description = "Start delivering shipment for a drone")
    @UpdateApiResponses
    @PostMapping(value = "/{droneId}/shipment/deliver", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ShipmentResponse> startDeliverDroneShipment(@PathVariable Long droneId) {
        Drone drone = droneService.findDrone(droneId);
        Shipment shipment = shipmentService.startDelivery(drone);
        return ResponseEntity.ok(new ShipmentResponse(shipment));
    }

    @Operation(summary = "Finish delivering shipment",
            description = "Finish delivering shipment for a drone")
    @UpdateApiResponses
    @PostMapping(value = "/{droneId}/shipment/deliver/finish", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ShipmentResponse> finishDeliverDroneShipment(@PathVariable Long droneId) {
        Drone drone = droneService.findDrone(droneId);
        Shipment shipment = shipmentService.finishDelivery(drone);
        return ResponseEntity.ok(new ShipmentResponse(shipment));
    }

    @Operation(summary = "Cancel shipment",
            description = "Cancel shipment for a drone")
    @UpdateApiResponses
    @PostMapping(value = "/{droneId}/shipment/cancel", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ShipmentResponse> cancelDroneShipment(@PathVariable Long droneId) {
        Drone drone = droneService.findDrone(droneId);
        Shipment shipment = shipmentService.cancelShipment(drone);
        return ResponseEntity.ok(new ShipmentResponse(shipment));
    }

    @Operation(summary = "Return drone",
            description = "Return drone")
    @UpdateApiResponses
    @PostMapping(value = "/{droneId}/return", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DroneResponse> returnDrone(@PathVariable Long droneId) {
        Drone drone = droneService.findDrone(droneId);
        drone = shipmentService.returnDrone(drone);
        return ResponseEntity.ok(new DroneResponse(drone));
    }

    @Operation(summary = "Finish returning drone",
            description = "Finish returning drone")
    @UpdateApiResponses
    @PostMapping(value = "/{droneId}/return/finish", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DroneResponse> finishReturnDrone(@PathVariable Long droneId) {
        Drone drone = droneService.findDrone(droneId);
        drone = shipmentService.finishReturnDrone(drone);
        return ResponseEntity.ok(new DroneResponse(drone));
    }
}
