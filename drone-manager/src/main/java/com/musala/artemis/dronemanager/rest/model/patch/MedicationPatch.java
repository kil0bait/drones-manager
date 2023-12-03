package com.musala.artemis.dronemanager.rest.model.patch;

import com.musala.artemis.dronemanager.model.Medication;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MedicationPatch {
    @DecimalMin(value = "0", inclusive = false)
    private Double weight;
    @Pattern(regexp = "[A-Z0-9_]+")
    private String code;

    public MedicationPatch(Medication medication) {
        this.weight = medication.getWeight();
        this.code = medication.getCode();
    }
}
