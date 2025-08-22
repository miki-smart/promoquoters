package com.backend.promoquoter.infrastructure.adapter.out.persistence.mapper;
import com.backend.promoquoter.infrastructure.adapter.in.web.dto.ProductDtos;
import com.backend.promoquoter.domain.model.Product;
import com.backend.promoquoter.infrastructure.adapter.out.persistence.entity.ProductEntity;

import java.util.List;
import java.util.stream.Collectors;

public final  class ProductMapper  {
    private ProductMapper() {}

    public  static Product toDomain(ProductEntity entity) {
        if (entity == null) return null;

        return new Product(
                entity.getId(),
                entity.getName(),
                entity.getCategory(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getStock(),
                entity.getVersion() // Add this argument
        );
    }

    public static ProductEntity toEntity(Product domain) {
        if (domain == null) return null;

        ProductEntity entity = new ProductEntity();
        
        // Only set ID if it's not null (for existing entities)
        // For new entities (ID null), let JPA generate the ID
        if (domain.getId() != null) {
            entity.setId(domain.getId());
        }
        
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setCategory(domain.getCategory());
        entity.setPrice(domain.getPrice());
        entity.setStock(domain.getStock());
        
        // Only set version if it's not null (for existing entities)
        // For new entities (version null), let JPA handle versioning automatically
        if (domain.getVersion() != null) {
            entity.setVersion(domain.getVersion());
        }
        
        return entity;
    }
}
