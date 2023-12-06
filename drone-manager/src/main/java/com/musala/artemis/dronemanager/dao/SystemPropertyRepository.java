package com.musala.artemis.dronemanager.dao;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class SystemPropertyRepository {
    private static final String FIND_PROP_QUERY = "SELECT value FROM system_property WHERE name = :name AND deleted <> true";

    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public String getSystemProperty(String name) {
        Object result = entityManager.createNativeQuery(FIND_PROP_QUERY, String.class)
                .setParameter("name", name).getSingleResult();
        return String.valueOf(result);
    }
}
