package com.backend.promoquoter.infrastructure.adapter.out.persistence.adapter;

import com.backend.promoquoter.application.port.out.IPromotionRepository;
import com.backend.promoquoter.domain.model.Promotion;
import com.backend.promoquoter.infrastructure.adapter.out.persistence.entity.PromotionEntity;
import com.backend.promoquoter.infrastructure.adapter.out.persistence.mapper.PromotionMapper;
import com.backend.promoquoter.infrastructure.adapter.out.persistence.repo.ProductJpaRepository;
import com.backend.promoquoter.infrastructure.adapter.out.persistence.repo.PromotionJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.backend.promoquoter.infrastructure.adapter.out.persistence.mapper.PromotionMapper.toDomain;

@Repository
public class PromotionJpaAdapter implements IPromotionRepository {
    private final PromotionJpaRepository promotionJpaRepository;
    public PromotionJpaAdapter(PromotionJpaRepository promotionJpaRepository) {
        this.promotionJpaRepository = promotionJpaRepository;
    }
    @Override
    public Optional<Promotion> findById(UUID id) {
        return this.promotionJpaRepository.findById(id).map(entity-> PromotionMapper.toDomain(entity));
    }

    @Override
    public Promotion savePromotion(Promotion promotion) {
        PromotionEntity entity = PromotionMapper.toEntity(promotion);
        PromotionEntity saved = this.promotionJpaRepository.save(entity);
        return PromotionMapper.toDomain(saved);
    }

    @Override
    public void deleteById(UUID id) {
        this.promotionJpaRepository.deleteById(id);
    }

    @Override
    public Promotion updatePromotion(Promotion promotion) {
        PromotionEntity entity = PromotionMapper.toEntity(promotion);
        PromotionEntity updated = this.promotionJpaRepository.save(entity);
        return PromotionMapper.toDomain(updated);
    }

    @Override
    public List<Promotion> findAllPromotions() {
        return this.promotionJpaRepository.findAll().stream().map(entity-> PromotionMapper.toDomain(entity)).toList();
    }


    @Override
    public List<Promotion> saveAll(List<Promotion> promotions) {
        List<PromotionEntity> promotionEntities = promotions.stream().map(entity-> PromotionMapper.toEntity(entity)).toList();
        List<PromotionEntity> savedEntities = this.promotionJpaRepository.saveAll(promotionEntities);
        return savedEntities.stream().map(PromotionMapper::toDomain).toList();
    }

    @Override
    public List<Promotion> findActiveBySegmentOrdered(String customerSegment) {
        return this.promotionJpaRepository.findByActiveTrueOrderByPriorityAsc()
                .stream().map(PromotionMapper::toDomain).toList();
    }


}
