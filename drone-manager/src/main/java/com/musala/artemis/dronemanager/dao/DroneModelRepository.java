package com.musala.artemis.dronemanager.dao;

import com.musala.artemis.dronemanager.model.DroneModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DroneModelRepository extends JpaRepository<DroneModel, Long> {
    Optional<DroneModel> findByName(String name);
}
