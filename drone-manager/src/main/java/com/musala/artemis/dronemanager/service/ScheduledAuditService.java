package com.musala.artemis.dronemanager.service;

import com.musala.artemis.dronemanager.dao.AuditDroneRepository;
import com.musala.artemis.dronemanager.dao.DroneRepository;
import com.musala.artemis.dronemanager.model.AuditDrone;
import com.musala.artemis.dronemanager.model.Drone;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledAuditService {
    private final DroneRepository droneRepository;
    private final AuditDroneRepository auditDroneRepository;

    @Scheduled(cron = "@midnight")
    public void logDronesBatteryCapacity() {
        List<Drone> drones = droneRepository.findAll();
        for (Drone drone : drones) {
            AuditDrone auditDrone = auditDroneRepository.save(createAuditDroneLog(drone));
            log.debug("Save AuditDrone: {}", auditDrone);
        }
        log.info("Finished task logDronesBatteryCapacity for {} available drones", drones.size());
    }

    private AuditDrone createAuditDroneLog(Drone drone) {
        return AuditDrone.builder()
                .drone(drone)
                .batteryCapacity(drone.getBatteryCapacity())
                .timestamp(new Date())
                .build();
    }
}
