package com.backend.promoquoter.application.port.out;

import com.backend.promoquoter.domain.model.Promotion;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IPromotionRepository {
    Optional<Promotion> findById(UUID id);
    Promotion savePromotion(Promotion promotion);
    void deleteById(UUID id);
    Promotion updatePromotion(Promotion promotion);
    List<Promotion> findAllPromotions();
    List<Promotion> saveAll(List<Promotion> promotions);
}
