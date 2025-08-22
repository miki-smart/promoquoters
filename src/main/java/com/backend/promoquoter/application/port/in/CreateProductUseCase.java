package com.backend.promoquoter.application.port.in;

import java.math.BigDecimal;
import java.util.List;

public interface CreateProductUseCase {
    List<ProductCreated> createProducts(List<CreateProductCommand> commands);

    record CreateProductCommand(
            String name,
            String category,
            BigDecimal price,
            int stock
    ) {}

    record ProductCreated(
            String id
    ) {}
}
