package com.musala.artemis.dronemanager.service;

import com.musala.artemis.dronemanager.dao.DroneModelRepository;
import com.musala.artemis.dronemanager.dao.DroneRepository;
import com.musala.artemis.dronemanager.exception.NonUniqueException;
import com.musala.artemis.dronemanager.exception.PrimaryNotFoundException;
import com.musala.artemis.dronemanager.exception.RelationNotFoundException;
import com.musala.artemis.dronemanager.model.Drone;
import com.musala.artemis.dronemanager.model.DroneModel;
import com.musala.artemis.dronemanager.model.DroneState;
import com.musala.artemis.dronemanager.rest.model.CreateDroneRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DroneManagerService {
    private final DroneRepository droneRepository;
    private final DroneModelRepository droneModelRepository;

    public List<Drone> findAllDrones() {
        return droneRepository.findAll();
    }

    public Drone addDrone(CreateDroneRequest createDroneRequest) throws RelationNotFoundException, NonUniqueException {
        DroneModel model = droneModelRepository.findByName(createDroneRequest.getModel())
                .orElseThrow(() -> new RelationNotFoundException(DroneModel.class, "name", createDroneRequest.getModel()));
        Optional<Drone> existing = droneRepository.findBySerialNumber(createDroneRequest.getSerialNumber());
        if (existing.isPresent())
            throw new NonUniqueException(Drone.class, "serialNumber", createDroneRequest.getSerialNumber());
        Drone drone = Drone.builder().serialNumber(createDroneRequest.getSerialNumber())
                .model(model)
                .state(DroneState.IDLE)
                .weightLimit(Optional.ofNullable(createDroneRequest.getWeightLimit()).orElse(model.getDefaultWeightLimit()))
                .batteryCapacity(100.0)
                .build();
        return droneRepository.save(drone);
    }

    public Drone findDrone(Long droneId) throws PrimaryNotFoundException {
        return droneRepository.findById(droneId)
                .orElseThrow(() -> new PrimaryNotFoundException(Drone.class, "id", String.valueOf(droneId)));
    }

    public void deleteDrone(Long droneId) throws PrimaryNotFoundException {
        Drone drone = findDrone(droneId);
        droneRepository.delete(drone);
    }

}
