package com.backend.promoquoter.application.port.out;

import com.backend.promoquoter.domain.model.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface IProductRepository {
    Optional<Product> findById(UUID id);
    List<Product> findByIds(List<UUID> ids);
    List<Product> findByIdsForUpdate(List<UUID> ids); // for pessimistic locking
    Product save(Product product);
    List<Product> saveAll(List<Product> products);
    void delete(UUID id);
    List<Product> findAll();
}

