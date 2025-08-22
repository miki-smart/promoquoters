// src/main/java/com/backend/promoquoter/infrastructure/adapter/in/web/dto/PromotionDtos.java
package com.backend.promoquoter.infrastructure.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromotionDtos {
    private UUID id;
    private String name;
    private String description;
    private BigDecimal discount;
    private String startDate;
    private String endDate;
}