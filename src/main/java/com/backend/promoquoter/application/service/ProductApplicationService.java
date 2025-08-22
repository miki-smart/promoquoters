package com.backend.promoquoter.application.service;

import com.backend.promoquoter.application.port.in.ProductUseCase;
import com.backend.promoquoter.application.port.out.IProductRepository;
import com.backend.promoquoter.domain.model.Product;
import com.backend.promoquoter.infrastructure.adapter.in.web.dto.ProductDtos;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ProductApplicationService implements ProductUseCase {
    private final IProductRepository productRepository;
    public ProductApplicationService(IProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    @Override
    public List<ProductDtos> createProducts(List<CreateProductCommand> commands) {
        return commands.stream().map(this::createProduct).toList();
    }
    public ProductDtos createProduct(CreateProductCommand command) {
        Product product =  Product.createNew(command.name(), command.category(),command.description(), command.price(),command.stock());
       Product saved= this.productRepository.save(product);
        return new ProductDtos(saved.getId(), saved.getName(), saved.getCategory(), saved.getDescription(), saved.getPrice(), saved.getStock());

    }

    @Override
    public ProductDtos updateProduct(UUID id, CreateProductCommand command) {
        var productOpt= this.productRepository.findById(id);
        if(productOpt.isEmpty()){
            throw new IllegalArgumentException("Product not found with id: " + id);
        }
        var product= new Product(id,command.name(), command.category(),command.description(), command.price(),command.stock(),productOpt.get().getVersion());
        var updated= this.productRepository.save(product);
        return new ProductDtos(updated.getId(), updated.getName(), updated.getCategory(), updated.getDescription(), updated.getPrice(), updated.getStock());

    }

    @Override
    public void deleteProduct(UUID id) {
        this.productRepository.delete(id);
    }

    @Override
    public List<ProductDtos> getProducts() {
        var products= this.productRepository.findAll();
        return products.stream().map(p-> new ProductDtos(p.getId(), p.getName(), p.getCategory(), p.getDescription(), p.getPrice(), p.getStock())).toList();
    }



}
