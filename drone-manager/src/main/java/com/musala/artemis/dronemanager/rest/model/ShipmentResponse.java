package com.musala.artemis.dronemanager.rest.model;

import com.musala.artemis.dronemanager.model.Medication;
import com.musala.artemis.dronemanager.model.Shipment;
import com.musala.artemis.dronemanager.model.ShipmentState;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ShipmentResponse {
    @NotNull
    @Schema(description = "Shipment ID")
    private Long id;
    @Schema(description = "Addressee name and address")
    private String addressee;
    @Schema(description = "Shipment start processing date")
    private Date startDate;
    @Schema(description = "Shipment end processing date")
    private Date endDate;
    @NotNull
    @Schema(description = "Delivery drone id")
    private Long droneId;
    @Schema(description = "Shipment medications IDs list")
    private List<Long> medicationIds;
    @Schema(description = "Shipment state")
    private ShipmentState shipmentState;

    public ShipmentResponse(Shipment shipment) {
        this.id = shipment.getId();
        this.addressee = shipment.getAddressee();
        this.startDate = shipment.getStartDate();
        this.endDate = shipment.getEndDate();
        this.droneId = shipment.getDrone().getId();
        this.shipmentState = shipment.getShipmentState();
        if (shipment.getMedications() != null)
            this.medicationIds = shipment.getMedications().stream().map(Medication::getId).toList();
        else
            this.medicationIds = new ArrayList<>(0);
    }
}
