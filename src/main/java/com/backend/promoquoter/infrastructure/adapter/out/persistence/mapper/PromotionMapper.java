package com.backend.promoquoter.infrastructure.adapter.out.persistence.mapper;

import com.backend.promoquoter.domain.model.Product;
import com.backend.promoquoter.domain.model.Promotion;
import com.backend.promoquoter.infrastructure.adapter.out.persistence.entity.PromotionEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public final class PromotionMapper {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private PromotionMapper() {}

    public static Promotion toDomain(PromotionEntity entity) {
        if (entity == null) return null;

        Map<String, Object> config = parseConfig(entity.getConfigJson());

        return new Promotion(
                entity.getId(),
                entity.getType(),
                config,
                entity.getPriority(),
                entity.getActive()
        );
    }

    public static PromotionEntity toEntity(Promotion domain) {
        if (domain == null) return null;

        PromotionEntity entity = new PromotionEntity();
        if (domain.getId() != null) {
            entity.setId(domain.getId());
        }
        entity.setType(domain.getType());
        entity.setConfigJson(serializeConfig(domain.getConfig()));
        entity.setPriority(domain.getPriority());
        entity.setActive(domain.isActive());
        return entity;
    }

    private static Map<String, Object> parseConfig(String configJson) {
        if (configJson == null || configJson.trim().isEmpty()) {
            return Map.of();
        }

        try {
            return objectMapper.readValue(configJson, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse promotion config JSON: " + configJson, e);
        }
    }

    private static String serializeConfig(Map<String, Object> config) {
        if (config == null || config.isEmpty()) {
            return "{}";
        }

        try {
            return objectMapper.writeValueAsString(config);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize promotion config to JSON", e);
        }
    }
}
