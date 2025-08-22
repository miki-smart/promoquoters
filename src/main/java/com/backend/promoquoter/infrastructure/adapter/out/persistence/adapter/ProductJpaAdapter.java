package com.backend.promoquoter.infrastructure.adapter.out.persistence.adapter;

import com.backend.promoquoter.application.port.out.IProductRepository;
import com.backend.promoquoter.domain.model.Product;
import com.backend.promoquoter.infrastructure.adapter.out.persistence.entity.ProductEntity;
import com.backend.promoquoter.infrastructure.adapter.out.persistence.mapper.ProductMapper;
import com.backend.promoquoter.infrastructure.adapter.out.persistence.repo.ProductJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class ProductJpaAdapter implements IProductRepository {
    private final ProductJpaRepository productJpaRepository;
    public ProductJpaAdapter(ProductJpaRepository productJpaRepository) {
        this.productJpaRepository = productJpaRepository;
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return productJpaRepository.findById(id).map(entity -> ProductMapper.toDomain(entity));
    }

    @Override
    public List<Product> findByIds(List<UUID> ids) {
        return productJpaRepository.findAllById(ids).stream().map(ProductMapper::toDomain).toList();
    }

    @Override
    public List<Product> findByIdsForUpdate(List<UUID> ids) {
        return productJpaRepository.findByIdInForUpdate(ids).stream().map(ProductMapper::toDomain).toList();
         }

    @Override
    public Product save(Product product) {
        ProductEntity entity = ProductMapper.toEntity(product);

        ProductEntity saved= productJpaRepository.save(entity);
        return ProductMapper.toDomain(saved);
    }

    @Override
    public List<Product> saveAll(List<Product> products) {
        List<ProductEntity> entities = products.stream().map(ProductMapper::toEntity).toList();
        List<ProductEntity> savedEntities = productJpaRepository.saveAll(entities);
        return savedEntities.stream().map(ProductMapper::toDomain).collect(Collectors.toList());

    }
    @Override
    public void delete(UUID id) {
        productJpaRepository.deleteById(id);
    }
    @Override
    public List<Product> findAll() {
        return productJpaRepository.findAll().stream().map(ProductMapper::toDomain).toList();
    }
}