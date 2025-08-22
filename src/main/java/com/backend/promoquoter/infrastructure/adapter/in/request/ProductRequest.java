package com.backend.promoquoter.infrastructure.adapter.in.request;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class ProductRequest {
    @NotBlank
    private String name;

    private String description;
    @DecimalMin(value="0.00", message = "price must be >= 0.00")
    private BigDecimal price;
    @Min(value=0, message = "stock must be >= 0")
    private final Integer stock;
    @NotBlank
    private String category;
}