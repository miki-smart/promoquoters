package com.backend.promoquoter.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
    private UUID id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, precision = 12, scale = 2)
    @DecimalMin(value = "0.00", inclusive = true, message = "price must be >= 0.00")
    private BigDecimal price;

    @Column(nullable = false)
    @Min(value = 0, message = "stock must be >= 0")
    private Integer stock;
    @Column(nullable = false)
    private String category;
    private String description;

    @Column(nullable = false) private Instant createdAt;
    @Column(nullable = false) private Instant updatedAt;

    @Version
    private Long version;

    @PrePersist
    public void prePersist() {
        createdAt = Instant.now();
        updatedAt = createdAt;
        validate();
    }
    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
        validate();
    }

    void validate() {
        if (price.compareTo(BigDecimal.ZERO) < 0 ) {
            throw new IllegalArgumentException("price must be >= 0.00");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("stock must be >= 0");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name cannot be null or blank");
        }
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("category cannot be null or blank");
        }
    }

}
