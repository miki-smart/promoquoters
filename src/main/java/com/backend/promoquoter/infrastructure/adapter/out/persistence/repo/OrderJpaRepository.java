package com.backend.promoquoter.infrastructure.adapter.out.persistence.repo;

import com.backend.promoquoter.infrastructure.adapter.out.persistence.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID> {

    Optional<OrderEntity> findByIdempotencyKey(String idempotencyKey);
    List<OrderEntity> findByCustomerSegmentOrderByCreatedAtDesc(String customerSegment);
}
