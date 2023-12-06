package com.musala.artemis.dronemanager.config;

import com.musala.artemis.dronemanager.dao.SystemPropertyRepository;
import com.musala.artemis.dronemanager.service.ShipmentValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DroneManagerConfig {
    private static final String MIN_BATTERY_LEVEL_PROPERTY = "battery_level_for_loading";
    private final SystemPropertyRepository systemPropertyRepository;

    @Bean
    public ShipmentValidation shipmentValidation() {
        String minBatteryLevel = systemPropertyRepository.getSystemProperty(MIN_BATTERY_LEVEL_PROPERTY);
        return new ShipmentValidation(Double.valueOf(minBatteryLevel));
    }
}
