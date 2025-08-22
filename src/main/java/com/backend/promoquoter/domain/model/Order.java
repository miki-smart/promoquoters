package com.backend.promoquoter.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class Order {
    private final UUID Id;
    private final List<OrderItem> Items;
    private final BigDecimal Total;
    public static Order create(List<OrderItem> items,BigDecimal total) {
        return new Order(UUID.randomUUID(),items,total);
    }
    @Getter
    @Setter
    public class OrderItem {
        private final UUID ProductId;
        private final int Quantity;
        private final BigDecimal Price;

        public OrderItem(UUID productId, int quantity, BigDecimal price) {
            this.ProductId = productId;
            this.Quantity = quantity;
            this.Price = price;
        }


    }
}

