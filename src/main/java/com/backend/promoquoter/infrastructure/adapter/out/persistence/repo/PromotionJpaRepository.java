package com.backend.promoquoter.infrastructure.adapter.out.persistence.repo;

import com.backend.promoquoter.infrastructure.adapter.out.persistence.entity.PromotionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PromotionJpaRepository extends JpaRepository<PromotionEntity, UUID> {
}
