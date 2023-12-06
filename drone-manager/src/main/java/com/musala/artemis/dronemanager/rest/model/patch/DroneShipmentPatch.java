package com.musala.artemis.dronemanager.rest.model.patch;

import com.musala.artemis.dronemanager.model.Medication;
import com.musala.artemis.dronemanager.model.Shipment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DroneShipmentPatch {
    private String addressee;
    private List<Long> medications;

    public DroneShipmentPatch(Shipment shipment) {
        this.addressee = shipment.getAddressee();
        if (shipment.getMedications() != null)
            this.medications = shipment.getMedications().stream().map(Medication::getId).toList();
        else
            this.medications = new ArrayList<>(0);
    }
}
