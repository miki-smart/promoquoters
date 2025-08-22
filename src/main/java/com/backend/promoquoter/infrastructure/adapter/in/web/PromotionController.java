package com.backend.promoquoter.infrastructure.adapter.in.web;

import com.backend.promoquoter.infrastructure.adapter.in.web.dto.PromotionDtos;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController("/api/v1/promotions")
public class PromotionController {
    @PostMapping("/create")
    private ResponseEntity<PromotionDtos> createPromotions(
            @Valid @RequestBody CreatePromotionRequest request
    ) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(null);
    }
    @GetMapping("/list")
    private ResponseEntity<List<PromotionDtos>> listPromotions() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(List.of());
    }

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
}
