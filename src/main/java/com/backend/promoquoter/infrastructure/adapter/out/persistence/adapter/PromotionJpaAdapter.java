package com.backend.promoquoter.infrastructure.adapter.out.persistence.adapter;

import com.backend.promoquoter.application.port.out.IPromotionRepository;
import com.backend.promoquoter.domain.model.Promotion;
import com.backend.promoquoter.infrastructure.adapter.out.persistence.repo.ProductJpaRepository;
import com.backend.promoquoter.infrastructure.adapter.out.persistence.repo.PromotionJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public class PromotionJpaAdapter implements IPromotionRepository {
    private final PromotionJpaRepository promotionJpaRepository;
    public PromotionJpaAdapter(PromotionJpaRepository promotionJpaRepository) {
        this.promotionJpaRepository = promotionJpaRepository;
    }
    @Override
    public Promotion findById(UUID id) {
        return null;
    }

    @Override
    public Promotion savePromotion(Promotion promotion) {
        return null;
    }

    @Override
    public void deleteById(UUID id) {

    }

    @Override
    public Promotion updatePromotion(Promotion promotion) {
        return null;
    }

    @Override
    public List<Promotion> findAllPromotions() {
        return List.of();
    }

    @Override
    public Promotion findPromotionByType(String type) {
        return null;
    }

    @Override
    public List<Promotion> saveAll(List<Promotion> promotions) {
        return List.of();
    }

    @Override
    public List<Promotion> findActivePromotions() {
        return List.of();
    }
}
