package com.backend.promoquoter.application.port.out;

import com.backend.promoquoter.domain.model.Product;

import java.util.List;
import java.util.UUID;

public interface IProductRepository {
    Product getProduct(String productId);
    void saveProduct(Product product);
    Product updateProduct(Product product);
    void deleteProduct(String productId);
    List<Product> getAllProducts();
    List<Product> findByIdsForUpdate(List<UUID> ids); // for pessimistic locking
}

