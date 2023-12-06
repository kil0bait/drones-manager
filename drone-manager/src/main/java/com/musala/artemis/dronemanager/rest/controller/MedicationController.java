package com.musala.artemis.dronemanager.rest.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.musala.artemis.dronemanager.model.Medication;
import com.musala.artemis.dronemanager.rest.annotation.CreatedApiResponses;
import com.musala.artemis.dronemanager.rest.annotation.DeleteApiResponses;
import com.musala.artemis.dronemanager.rest.annotation.FindByIdApiResponses;
import com.musala.artemis.dronemanager.rest.annotation.UpdateApiResponses;
import com.musala.artemis.dronemanager.rest.model.CreateMedicationRequest;
import com.musala.artemis.dronemanager.rest.model.MedicationResponse;
import com.musala.artemis.dronemanager.rest.model.PatchDocument;
import com.musala.artemis.dronemanager.service.MedicationService;
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
@RequestMapping("/rest/v1/medication")
@Tag(name = "Medication CRUD")
@RequiredArgsConstructor
public class MedicationController {
    private final MedicationService medicationService;

    @Operation(summary = "Get all medications",
            description = "Get all medications. Standard CRUD Read-All operation")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MedicationResponse>> getAllMedications() {
        List<MedicationResponse> list = medicationService.findAllMedications().stream().map(MedicationResponse::new).toList();
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Create medication",
            description = "Create medication. Standard CRUD Create operation")
    @CreatedApiResponses
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MedicationResponse> createMedication(@RequestBody @Validated CreateMedicationRequest createMedicationRequest) {
        Medication medication = medicationService.addMedication(createMedicationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MedicationResponse(medication));
    }

    @Operation(summary = "Get medication",
            description = "Get medication by id. Standard CRUD Read operation")
    @FindByIdApiResponses
    @GetMapping(value = "/{medicationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MedicationResponse> getMedication(@PathVariable Long medicationId) {
        Medication medication = medicationService.findMedication(medicationId);
        return ResponseEntity.ok(new MedicationResponse(medication));
    }

    @Operation(summary = "Update medication",
            description = "Update medication parameters by id. Standard CRUD Update operation",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PatchDocument.class)))))
    @UpdateApiResponses
    @PatchMapping(value = "/{medicationId}", consumes = "application/json-patch+json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MedicationResponse> updateMedication(@PathVariable Long medicationId, @RequestBody @Validated JsonPatch jsonPatch)
            throws JsonPatchException, JsonProcessingException {
        Medication medication = medicationService.patchMedication(medicationId, jsonPatch);
        return ResponseEntity.ok(new MedicationResponse(medication));
    }

    @Operation(summary = "Delete medication",
            description = "Delete medication by id. Standard CRUD Delete operation")
    @DeleteApiResponses
    @DeleteMapping(value = "/{medicationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteMedication(@PathVariable Long medicationId) {
        medicationService.deleteMedication(medicationId);
        return ResponseEntity.noContent().build();
    }

}
