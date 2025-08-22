package com.backend.promoquoter.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;


public record ProductDtos (
     UUID id,
    @NotBlank(message = "name is mandatory")
     String name,
    @NotBlank(message = "category is mandatory")
     String category,

     String description,
    @DecimalMin(value="0.00", message = "price must be >= 0.00")
     BigDecimal price,
    @Min(value=0, message = "stock must be >= 0")
     Integer stock

){}
