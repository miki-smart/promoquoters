package com.backend.promoquoter.domain.model;

import java.util.Map;
import java.util.UUID;

public class Promotion {
    private final UUID id;
    private final String type; // e.g., PERCENT_OFF_CATEGORY, BUY_X_GET_Y
    private final Map<String, Object> config;
    private final int priority;
    private final boolean active;

    public Promotion(UUID id, String type, Map<String, Object> config, int priority, boolean active) {
        this.id = id;
        this.type = type;
        this.config = config;
        this.priority = priority;
        this.active = active;
    }
    public static Promotion create(String type, Map<String, Object> config, int priority, boolean active) {
        return new Promotion(UUID.randomUUID(), type, config, priority, active);
    }
    public static String requireNonEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty");
        }
        return value.trim();
    }
}
