package com.musala.artemis.dronemanager.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class Drone {
    @Id
    private String serialNumber;
}
