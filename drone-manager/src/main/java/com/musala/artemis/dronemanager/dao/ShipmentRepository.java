package com.musala.artemis.dronemanager.dao;

import com.musala.artemis.dronemanager.model.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    Optional<Shipment> findByDroneId(Long droneId);

    @Query("""
            SELECT s FROM Shipment s \s
            WHERE s.drone.id = :droneId AND s.shipmentState NOT IN ('DELIVERED', 'CANCELLED')
            """)
    Optional<Shipment> findByDroneIdInActiveState(Long droneId);
}
