package com.musala.artemis.dronemanager.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.musala.artemis.dronemanager.dao.MedicationRepository;
import com.musala.artemis.dronemanager.exception.NonUniqueException;
import com.musala.artemis.dronemanager.exception.PatchValidationException;
import com.musala.artemis.dronemanager.exception.PrimaryNotFoundException;
import com.musala.artemis.dronemanager.model.Medication;
import com.musala.artemis.dronemanager.rest.model.CreateMedicationRequest;
import com.musala.artemis.dronemanager.rest.model.patch.MedicationPatch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MedicationService {
    private final MedicationRepository medicationRepository;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    public List<Medication> findAllMedications() {
        return medicationRepository.findAll();
    }

    public Medication addMedication(CreateMedicationRequest createMedicationRequest) {
        Optional<Medication> existing = medicationRepository.findByName(createMedicationRequest.getName());
        if (existing.isPresent())
            throw new NonUniqueException(Medication.class, "name", createMedicationRequest.getName());
        Medication medication = Medication.builder()
                .name(createMedicationRequest.getName())
                .weight(createMedicationRequest.getWeight())
                .code(createMedicationRequest.getCode())
                .build();
        return medicationRepository.save(medication);
    }

    public Medication findMedication(Long medicationId) {
        return medicationRepository.findById(medicationId)
                .orElseThrow(() -> new PrimaryNotFoundException(Medication.class, "id", String.valueOf(medicationId)));
    }

    public Medication patchMedication(Long medicationId, JsonPatch jsonPatch) throws JsonPatchException, JsonProcessingException {
        Medication medication = findMedication(medicationId);
        MedicationPatch medicationPatch = new MedicationPatch(medication);
        JsonNode patched = jsonPatch.apply(objectMapper.convertValue(medicationPatch, JsonNode.class));
        MedicationPatch patchedModel = objectMapper.treeToValue(patched, MedicationPatch.class);
        Errors errors = validator.validateObject(patchedModel);
        if (errors.hasErrors())
            throw new PatchValidationException(errors);
        applyChanges(medication, patchedModel);
        return updateMedication(medication);
    }

    public Medication updateMedication(Medication medication) {
        return medicationRepository.save(medication);
    }

    public void deleteMedication(Long medicationId) {
        Medication medication = findMedication(medicationId);
        medicationRepository.delete(medication);
    }

    private void applyChanges(Medication orig, MedicationPatch patched) {
        orig.setWeight(patched.getWeight());
        orig.setCode(patched.getCode());
    }
}
