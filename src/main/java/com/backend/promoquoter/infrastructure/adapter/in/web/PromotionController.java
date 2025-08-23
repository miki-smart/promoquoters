package com.backend.promoquoter.infrastructure.adapter.in.web;

import com.backend.promoquoter.application.port.in.CreatePromotionUseCase;
import com.backend.promoquoter.domain.model.Promotion;
import com.backend.promoquoter.infrastructure.adapter.in.request.CreatePromotionRequest;
import com.backend.promoquoter.infrastructure.adapter.in.web.dto.PromotionDtos;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/promotions")
public class PromotionController {
    private  final CreatePromotionUseCase createPromotionUseCase;
    public PromotionController(CreatePromotionUseCase createPromotionUseCase) {
        this.createPromotionUseCase = createPromotionUseCase;
    }

    @PostMapping("/create")
    public ResponseEntity<List<PromotionCreatedDto>> create(@Valid @RequestBody List<CreatePromotionDto> request) {
        // Map DTOs to commands
        List<CreatePromotionUseCase.CreatePromotionCommand> commands = request.stream()
                .map(dto -> new CreatePromotionUseCase.CreatePromotionCommand(
                        dto.type(),
                        dto.config(),
                        dto.priority(),
                        dto.active()
                ))
                .toList();

        // Execute use case
        List<CreatePromotionUseCase.PromotionCreated> results = createPromotionUseCase.createPromotions(commands);

        // Map results to DTOs
        List<PromotionCreatedDto> response = results.stream()
                .map(result -> new PromotionCreatedDto(result.id()))
                .toList();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public record CreatePromotionDto(
            @NotBlank String type,
            @NotNull Map<String, Object> config,
            int priority,
            boolean active
    ) {}

    public record PromotionCreatedDto(String id) {}


}
