package com.musala.artemis.dronemanager.rest.model;

import com.musala.artemis.dronemanager.model.Medication;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MedicationResponse {
    @NotNull
    @Schema(description = "Medication ID")
    private Long id;
    @NotBlank
    @Pattern(regexp = "[a-zA-Z0-9\\-_]+")
    @Schema(description = "Medication name. Is unique for each medication")
    private String name;
    @NotNull
    @DecimalMin(value = "0", inclusive = false)
    @Schema(description = "Medication weight in grams")
    private Double weight;
    @NotBlank
    @Pattern(regexp = "[A-Z0-9_]+")
    @Schema(description = "Medication code")
    private String code;
    @Schema(description = "Image of medication ID")
    private String imageId;

    public MedicationResponse(Medication medication) {
        this.id = medication.getId();
        this.name = medication.getName();
        this.weight = medication.getWeight();
        this.code = medication.getCode();
        this.imageId = medication.getImageId();
    }
}
