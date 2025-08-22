package com.backend.promoquoter.infrastructure.adapter.in.web;
import com.backend.promoquoter.application.port.in.ProductUseCase;
import com.backend.promoquoter.infrastructure.adapter.in.request.ProductRequest;
import com.backend.promoquoter.infrastructure.adapter.in.web.dto.ProductDtos;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductUseCase createProductUseCase;
    public ProductController(ProductUseCase createProductUseCase) {
        this.createProductUseCase = createProductUseCase;
    }
    @PostMapping("/create")
    public ResponseEntity<List<ProductDtos>> createProduct(@Valid @RequestBody List<ProductRequest> requests) {
        List<ProductUseCase.CreateProductCommand> command=requests.stream().map(dto->
                new ProductUseCase.CreateProductCommand(
                        dto.getName(),
                        dto.getCategory(),
                        dto.getDescription(),
                        dto.getPrice(),
                        dto.getStock()
                )).toList();
        var products= createProductUseCase.createProducts(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(products);
    }
    @PutMapping("/{productId}")
    public ResponseEntity<ProductDtos> updateProduct(@RequestBody ProductDtos productDtos, @PathVariable UUID productId) {
        var command= new ProductUseCase.CreateProductCommand(
                productDtos.name(),
                productDtos.category(),
                productDtos.description(),
                productDtos.price(),
                productDtos.stock()
        );
        var updated= createProductUseCase.updateProduct(productId, command);
        return ResponseEntity.ok(updated);
    }
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID productId) {
        createProductUseCase.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }
    @GetMapping
    public ResponseEntity<List<ProductDtos>> listProducts() {
        return ResponseEntity.ok(createProductUseCase.getProducts());
    }


    @ExceptionHandler({IllegalArgumentException.class, ConstraintViolationException.class, DataIntegrityViolationException.class})
    public ResponseEntity<Map<String,Object>> handleBadRequest(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", 400);
        body.put("error", "Bad Request");
        body.put("message", ex.getMessage());
        return ResponseEntity.badRequest().body(body);
    }
    @ExceptionHandler({ObjectOptimisticLockingFailureException.class})
    public ResponseEntity<Map<String,Object>> handleConflict(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", 409);
        body.put("error", "Conflict");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }
}

