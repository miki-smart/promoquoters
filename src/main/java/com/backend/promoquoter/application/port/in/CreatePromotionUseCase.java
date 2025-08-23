package com.backend.promoquoter.application.port.in;

import java.util.List;
import java.util.Map;

public interface CreatePromotionUseCase {
    List<PromotionCreated> createPromotions(List<CreatePromotionCommand> commands);

    record CreatePromotionCommand(
            String type,
            Map<String, Object> config,
            int priority,
            boolean active
    ) {}

    record PromotionCreated(
            String id,
            String type,
            Map<String, Object> config,
            int priority,
            boolean active
    ) {}
}
