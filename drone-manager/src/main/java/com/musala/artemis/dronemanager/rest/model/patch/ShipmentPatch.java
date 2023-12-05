package com.musala.artemis.dronemanager.rest.model.patch;

import com.musala.artemis.dronemanager.model.Shipment;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ShipmentPatch {
    @NotBlank
    @Schema(description = "Addressee name and address")
    private String addressee;

    public ShipmentPatch(Shipment shipment) {
        this.addressee = shipment.getAddressee();
    }
}
