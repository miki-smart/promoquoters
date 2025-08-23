package com.backend.promoquoter.application.port.out;

import com.backend.promoquoter.domain.model.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IOrderRepository {
    Optional<Order> findById(UUID id);
    Optional<Order> findByIdempotencyKey(String idempotencyKey);
    Order save(Order order);
    List<Order> saveAll(List<Order> orders);
    List<Order> findByCustomerSegment(String customerSegment);
}
