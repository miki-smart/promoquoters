package com.backend.promoquoter.infrastructure.adapter.out.persistence.mapper;
import com.backend.promoquoter.domain.model.Order;
import com.backend.promoquoter.infrastructure.adapter.out.persistence.entity.OrderEntity;
import com.backend.promoquoter.infrastructure.adapter.out.persistence.entity.OrderItemEntity;
import com.backend.promoquoter.infrastructure.adapter.out.persistence.entity.OrderPromotionEntity;
import java.util.List;
import java.util.UUID;


public class OrderMapper {

    public static Order toDomain(OrderEntity entity) {
        if (entity == null) {
            return null;
        }

        List<Order.OrderItem> orderItems = entity.getItems().stream()
                .map(item -> new Order.OrderItem(
                        item.getProductId(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getLineDiscount(),
                        item.getLineTotal()
                ))
                .toList();

        List<Order.AppliedPromotion> promotions = entity.getAppliedPromotions().stream()
                .map(promo -> new Order.AppliedPromotion(
                        promo.getPromotionId(),
                        promo.getType(),
                        promo.getDescription(),
                        promo.getAmount(),
                        promo.getPriority()
                ))
                .toList();

        return new Order(
                entity.getId(),
                orderItems,
                promotions,
                entity.getSubtotal(),
                entity.getTotalDiscount(),
                entity.getTotal(),
                entity.getCustomerSegment(),
                entity.getIdempotencyKey(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                Order.OrderStatus.valueOf(entity.getStatus().name())
        );
    }

    public static OrderEntity toEntity(Order domain) {
        if (domain == null) {
            return null;
        }

        OrderEntity entity = new OrderEntity(
                domain.getId(),
                domain.getSubtotal(),
                domain.getTotalDiscount(),
                domain.getTotal(),
                domain.getCustomerSegment(),
                domain.getIdempotencyKey(),
                OrderEntity.OrderStatus.valueOf(domain.getStatus().name())
        );

        // Set timestamps if available
        if (domain.getCreatedAt() != null) {
            entity.setCreatedAt(domain.getCreatedAt());
        }
        if (domain.getUpdatedAt() != null) {
            entity.setUpdatedAt(domain.getUpdatedAt());
        }

        // Map order items
        List<OrderItemEntity> itemEntities = domain.getItems().stream()
                .map(item -> new OrderItemEntity(
                        UUID.randomUUID(),
                        entity,
                        item.getProductId(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getLineDiscount(),
                        item.getLineTotal()
                ))
                .toList();
        entity.setItems(itemEntities);

        // Map applied promotions
        List<OrderPromotionEntity> promotionEntities = domain.getAppliedPromotions().stream()
                .map(promo -> new OrderPromotionEntity(
                        UUID.randomUUID(),
                        entity,
                        promo.getPromotionId(),
                        promo.getType(),
                        promo.getDescription(),
                        promo.getAmount(),
                        promo.getPriority()
                ))
                .toList();
        entity.setAppliedPromotions(promotionEntities);

        return entity;
    }
}
