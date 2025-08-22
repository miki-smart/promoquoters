package com.backend.promoquoter.application.port.out;

import com.backend.promoquoter.domain.model.Promotion;

import java.util.List;
import java.util.UUID;

public interface IPromotionRepository {
    Promotion findById(UUID id);
    Promotion save(Promotion promotion);
    void deleteById(UUID id);
    Promotion updatePromotion(Promotion promotion);
    List<Promotion> findAllPromotions();
    Promotion findPromotionByType(String type);
    List<Promotion> saveAll(List<Promotion> promotions);
    List<Promotion> findActivePromotions();
}
