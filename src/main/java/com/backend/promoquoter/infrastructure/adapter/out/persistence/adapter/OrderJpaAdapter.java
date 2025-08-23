package com.backend.promoquoter.infrastructure.adapter.out.persistence.adapter;

import com.backend.promoquoter.application.port.out.IOrderRepository;
import com.backend.promoquoter.domain.model.Order;
import com.backend.promoquoter.infrastructure.adapter.out.persistence.entity.OrderEntity;
import com.backend.promoquoter.infrastructure.adapter.out.persistence.mapper.OrderMapper;
import com.backend.promoquoter.infrastructure.adapter.out.persistence.repo.OrderJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class OrderJpaAdapter implements IOrderRepository {

    private final OrderJpaRepository jpaRepository;

    public OrderJpaAdapter(OrderJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(OrderMapper::toDomain);
    }

    @Override
    public Optional<Order> findByIdempotencyKey(String idempotencyKey) {
        return jpaRepository.findByIdempotencyKey(idempotencyKey)
                .map(OrderMapper::toDomain);
    }

    @Override
    public Order save(Order order) {
        OrderEntity entity = OrderMapper.toEntity(order);
        OrderEntity saved = jpaRepository.save(entity);
        return OrderMapper.toDomain(saved);
    }

    @Override
    public List<Order> saveAll(List<Order> orders) {
        if (orders == null || orders.isEmpty()) {
            return List.of();
        }

        List<OrderEntity> entities = orders.stream()
                .map(OrderMapper::toEntity)
                .toList();

        List<OrderEntity> saved = jpaRepository.saveAll(entities);

        return saved.stream()
                .map(OrderMapper::toDomain)
                .toList();
    }

    @Override
    public List<Order> findByCustomerSegment(String customerSegment) {
        return jpaRepository.findByCustomerSegmentOrderByCreatedAtDesc(customerSegment)
                .stream()
                .map(OrderMapper::toDomain)
                .toList();
    }


}