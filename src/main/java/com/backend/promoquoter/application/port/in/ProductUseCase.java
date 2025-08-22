package com.backend.promoquoter.application.port.in;

import com.backend.promoquoter.infrastructure.adapter.in.web.dto.ProductDtos;
import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;

public interface ProductUseCase {
    List<ProductDtos> createProducts(List<CreateProductCommand> commands);
    ProductDtos updateProduct(UUID productId, CreateProductCommand command);
    void deleteProduct(UUID productId);
    List<ProductDtos> getProducts();

    record CreateProductCommand(
            String name,
            String category,
            String description,
            BigDecimal price,
            Integer stock
    ) {}
}