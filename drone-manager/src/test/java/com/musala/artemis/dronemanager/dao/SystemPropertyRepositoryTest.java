package com.musala.artemis.dronemanager.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;NON_KEYWORDS=value",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SystemPropertyRepositoryTest {

    @Autowired
    SystemPropertyRepository systemPropertyRepository;

    @Test
    void getSystemProperty_success() {
        String fleetSize = systemPropertyRepository.getSystemProperty("fleet_size");

        assertEquals("10", fleetSize);
    }

    @Test
    void getSystemProperty_fail() {
        assertThrowsExactly(EmptyResultDataAccessException.class,
                () -> systemPropertyRepository.getSystemProperty("property1"));
    }
}