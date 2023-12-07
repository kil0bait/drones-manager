package com.musala.artemis.dronemanager.dao;

import com.musala.artemis.dronemanager.model.AuditDrone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditDroneRepository extends JpaRepository<AuditDrone, Long> {
}
