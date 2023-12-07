package com.musala.artemis.dronemanager.config;

import com.musala.artemis.dronemanager.dao.SystemPropertyRepository;
import com.musala.artemis.dronemanager.service.ValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DroneManagerConfig {
    private static final String MIN_BATTERY_LEVEL_PROPERTY = "battery_level_for_loading";
    private static final String FLEET_SIZE = "fleet_size";

    private final SystemPropertyRepository systemPropertyRepository;

    @Bean
    public ValidationService validationService() {
        String minBatteryLevel = systemPropertyRepository.getSystemProperty(MIN_BATTERY_LEVEL_PROPERTY);
        String fleetSize = systemPropertyRepository.getSystemProperty(FLEET_SIZE);
        return new ValidationService(Double.valueOf(minBatteryLevel), Long.valueOf(fleetSize));
    }
}
