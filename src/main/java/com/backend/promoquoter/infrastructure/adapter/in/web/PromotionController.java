package com.backend.promoquoter.infrastructure.adapter.in.web;

import com.backend.promoquoter.infrastructure.adapter.in.request.CreatePromotionRequest;
import com.backend.promoquoter.infrastructure.adapter.in.web.dto.PromotionDtos;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/promotions")
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


}
