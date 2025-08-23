package com.backend.promoquoter.domain.model;

import com.backend.promoquoter.domain.pricing.PricingContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;public class Order {
    private final UUID id;
    private final List<OrderItem> items;
    private final List<AppliedPromotion> appliedPromotions;
    private final BigDecimal subtotal;
    private final BigDecimal totalDiscount;
    private final BigDecimal total;
    private final String customerSegment;
    private final String idempotencyKey;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final OrderStatus status;

    public Order(UUID id, List<OrderItem> items, List<AppliedPromotion> appliedPromotions,
                 BigDecimal subtotal, BigDecimal totalDiscount, BigDecimal total,
                 String customerSegment, String idempotencyKey,
                 LocalDateTime createdAt, LocalDateTime updatedAt, OrderStatus status) {
        this.id = id;
        this.items = List.copyOf(items);
        this.appliedPromotions = List.copyOf(appliedPromotions);
        this.subtotal = subtotal;
        this.totalDiscount = totalDiscount;
        this.total = total;
        this.customerSegment = customerSegment;
        this.idempotencyKey = idempotencyKey;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.status = status;
    }

    // Factory method for creating from pricing context
    public static Order createFromPricingContext(PricingContext context, String customerSegment, String idempotencyKey) {
        List<OrderItem> orderItems = context.getItems().stream()
                .map(item -> new OrderItem(
                        item.getProductId(),
                        item.getName(),
                        item.getQty(),
                        item.getUnitPrice(),
                        item.getDiscount(),
                        item.getLineTotal()
                ))
                .toList();

        List<AppliedPromotion> promotions = context.getAppliedPromotions().stream()
                .map(promo -> new AppliedPromotion(
                        promo.promotionId(),
                        promo.type(),
                        promo.description(),
                        promo.amount(),
                        promo.priority()
                ))
                .toList();

        LocalDateTime now = LocalDateTime.now();
        return new Order(
                UUID.randomUUID(),
                orderItems,
                promotions,
                context.getSubtotal(),
                context.getTotalDiscount(),
                context.getTotal(),
                customerSegment,
                idempotencyKey,
                now,
                now,
                OrderStatus.CONFIRMED
        );
    }

    // Getters
    public UUID getId() { return id; }
    public List<OrderItem> getItems() { return items; }
    public List<AppliedPromotion> getAppliedPromotions() { return appliedPromotions; }
    public BigDecimal getSubtotal() { return subtotal; }
    public BigDecimal getTotalDiscount() { return totalDiscount; }
    public BigDecimal getTotal() { return total; }
    public String getCustomerSegment() { return customerSegment; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public OrderStatus getStatus() { return status; }

    public static class OrderItem {
        private final UUID productId;
        private final String productName;
        private final int quantity;
        private final BigDecimal unitPrice;
        private final BigDecimal lineDiscount;
        private final BigDecimal lineTotal;

        public OrderItem(UUID productId, String productName, int quantity, 
                        BigDecimal unitPrice, BigDecimal lineDiscount, BigDecimal lineTotal) {
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.lineDiscount = lineDiscount;
            this.lineTotal = lineTotal;
        }

        public UUID getProductId() { return productId; }
        public String getProductName() { return productName; }
        public int getQuantity() { return quantity; }
        public BigDecimal getUnitPrice() { return unitPrice; }
        public BigDecimal getLineDiscount() { return lineDiscount; }
        public BigDecimal getLineTotal() { return lineTotal; }
    }

    public static class AppliedPromotion {
        private final UUID promotionId;
        private final String type;
        private final String description;
        private final BigDecimal amount;
        private final int priority;

        public AppliedPromotion(UUID promotionId, String type, String description, 
                               BigDecimal amount, int priority) {
            this.promotionId = promotionId;
            this.type = type;
            this.description = description;
            this.amount = amount;
            this.priority = priority;
        }

        public UUID getPromotionId() { return promotionId; }
        public String getType() { return type; }
        public String getDescription() { return description; }
        public BigDecimal getAmount() { return amount; }
        public int getPriority() { return priority; }
    }

    public enum OrderStatus {
        CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED
    }
}


