package com.backend.promoquoter.domain.model;

import com.backend.promoquoter.infrastructure.adapter.in.web.dto.ProductDtos;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;
@Getter
@Setter
public class Product
{
   private final UUID id;
   private final String name;
    private final String category;

   private final String description;
    private final BigDecimal price;
    private final  Integer stock;


    public Product(UUID id, String name, String category, String description, BigDecimal price, Integer stock) {
        this.id = id;
        this.name = requireNonEmpty(name, "name");
        this.category = requireNonEmpty(category, "category");
        this.description = requireNonEmpty(description, "description");
        this.price = requireNonNegative(price, "price");
        this.stock = requireNonNegative(stock, "stock");
    }

    //Factory Method for new products
    public static Product createNew( String name, String category, String description, BigDecimal price, Integer stock) {
        return new Product(UUID.randomUUID(), name, category, description, price, stock);
    }
    public void reserveStock(int quantity) {
        if(stock<quantity) {
            throw new InSufficentStockException(
                    String.format("Cannot reserve %d items, only %d available for product %s", quantity, stock, id)
            );
        }
    }
    public boolean isAvailable(int quantity) {
        return stock >= quantity;
    }

    public static String requireNonEmpty(String value, String field) {
        if(value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " cannot be null or empty");
        }
        return value.trim();
    }

    public static BigDecimal requireNonNegative(BigDecimal value, String field) {
        if(value == null || value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(field + " must be >= 0.00");
        }
        return value;
    }
    public static int requireNonNegative(int value, String field) {
        if(value < 0) {
            throw new IllegalArgumentException(field + " must be >= 0");
        }
        return value;
    }


    public static class InSufficentStockException extends RuntimeException {
        public InSufficentStockException(String message) {
            super(message);

        }
    }
}
// Exception class

