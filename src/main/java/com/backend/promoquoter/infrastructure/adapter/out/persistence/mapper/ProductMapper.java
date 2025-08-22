package com.backend.promoquoter.infrastructure.adapter.out.persistence.mapper;
import com.backend.promoquoter.infrastructure.adapter.in.web.dto.ProductDtos;
import com.backend.promoquoter.infrastructure.adapter.out.persistence.dto.ProductDto;
import com.backend.promoquoter.domain.model.Product;

import java.util.List;
import java.util.stream.Collectors;

public class ProductMapper {
    // This class will contain methods to map between Product entities and DTOs

     public ProductDtos toDto(Product product) {
         // Mapping logic here
         return new ProductDtos(product.getId(), product.getName(), product.getPrice());
     }
     public Product toEntity(ProductDtos productDto) {
         // Mapping logic here
         return new Product(productDto.getId(), productDto.getName(), productDto.get
     price());
     }
     public List<ProductDtos> toDtoList(List<Product> products) {
         return products.stream()
                 .map(this::toDto)
                 .collect(Collectors.toList());
     }

     public List<Product> toEntityList(List<ProductDtos> productDtos) {
         return productDtos.stream()
                 .map(this::toEntity)
                 .collect(Collectors.toList());
     }


}
