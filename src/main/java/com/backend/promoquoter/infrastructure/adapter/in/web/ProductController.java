package com.backend.promoquoter.infrastructure.adapter.in.web;

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
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController(value = "/products")
public class ProductController {

    @PostMapping("/products")
    public ResponseEntity<ProductDtos> createProduct(@Valid @RequestBody ProductRequest dto) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(null);
    }
    @PostMapping("/products/validate")
    public ResponseEntity<ProductDtos> validateProduct() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(null);
    }
    @PutMapping("/products/{productId}")
    public ResponseEntity<ProductDtos> updateProduct(@RequestBody ProductDtos productDtos, @PathVariable String productId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(null);
    }
    @GetMapping("/products/{productId}")
    public ResponseEntity<ProductDtos> getProduct(@PathVariable String productId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(null);
    }

    @Getter
    @Setter
    @NoArgsConstructor(force = true)
    @AllArgsConstructor
    public class ProductRequest {
        @NotBlank
        private String name;

        private String description;
        @DecimalMin(value="0.00", message = "price must be >= 0.00")
        private BigDecimal price;
        @Min(value=0, message = "stock must be >= 0")
        private final Integer stock;
        @NotBlank
        private String category;
    }
    @ExceptionHandler({IllegalArgumentException.class, ConstraintViolationException.class, DataIntegrityViolationException.class})
    public ResponseEntity<Map<String,Object>> handleBadRequest(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", 400);
        body.put("error", "Bad Request");
        body.put("message", ex.getMessage());
        return ResponseEntity.badRequest().body(body);
    }
}
