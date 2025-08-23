// src/main/java/com/backend/promoquoter/infrastructure/adapter/in/web/dto/PromotionDtos.java
package com.backend.promoquoter.infrastructure.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;
import java.math.BigDecimal;


public record PromotionDtos (
        UUID id,
        String name,
        String description,
        BigDecimal discount,
        String startDate,
        String endDate
){}
