package com.backend.promoquoter.infrastructure.adapter.in.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class CreatePromotionRequest {

    private final String type; // e.g., PERCENT_OFF_CATEGORY, BUY_X_GET_Y
    private final Map<String, Object> config;
    private final int priority;
    private final boolean active;
}
