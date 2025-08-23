package com.backend.promoquoter.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_promotions")
@Getter
@Setter
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class OrderPromotionEntity {

    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @Column(name = "promotion_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID promotionId;

    @Column(name = "type", nullable = false, length = 50)
    private String type;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "priority", nullable = false)
    private Integer priority;
}
