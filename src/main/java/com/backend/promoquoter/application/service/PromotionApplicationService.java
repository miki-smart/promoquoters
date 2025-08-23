package com.backend.promoquoter.application.service;

import com.backend.promoquoter.application.port.in.CreatePromotionUseCase;
import com.backend.promoquoter.application.port.out.IPromotionRepository;
import com.backend.promoquoter.domain.model.Promotion;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
@Service
@Transactional
public class PromotionApplicationService implements CreatePromotionUseCase {
    private final IPromotionRepository promotionRepository;
    public PromotionApplicationService(IPromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    @Override
    public List<PromotionCreated> createPromotions(List<CreatePromotionCommand> commands) {
        return commands.stream().map(this::createPromotion).toList();
    }
    private PromotionCreated createPromotion(CreatePromotionCommand command) {
        validatePromotionType(command.type());
        var promotion= Promotion.create(command.type(),command.config(),command.priority(),command.active());
        var saved= this.promotionRepository.savePromotion(promotion);
        return new PromotionCreated(saved.getId().toString(),saved.getType(),saved.getConfig(),saved.getPriority(),saved.isActive());
    }
    private void validatePromotionType(String type) {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Promotion type cannot be null or empty");
        }

        // Validate known promotion types
        List<String> validTypes = List.of("PERCENT_OFF_CATEGORY", "BUY_X_GET_Y");
        if (!validTypes.contains(type.trim().toUpperCase())) {
            throw new IllegalArgumentException(
                    "Unknown promotion type: " + type + ". Valid types: " + validTypes
            );
        }
    }
}
