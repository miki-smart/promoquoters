package com.backend.promoquoter.application.service;

import com.backend.promoquoter.application.port.in.ProductUseCase;
import com.backend.promoquoter.application.port.out.IProductRepository;
import com.backend.promoquoter.domain.model.Product;
import com.backend.promoquoter.infrastructure.adapter.in.web.dto.ProductDtos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Product Application Service Test")
class ProductApplicationServiceTest {

    @Mock
    private IProductRepository productRepository;
    private ProductApplicationService productApplicationService;


    @BeforeEach
    void setUp() {
     productApplicationService = new ProductApplicationService(productRepository);
    }
    @Nested
    @DisplayName("Create Products Tests")
    class CreateProductsTests {

        @Test
        @DisplayName("Should create single product successfully")
        void shouldCreateSingleProductSuccessfully() {
            // Given
            UUID productId = UUID.randomUUID();
            Product savedProduct = new Product(productId, "Gaming Laptop",
                    "electronics",
                    "electronics",
                    new BigDecimal("1200.00"), 50, 0L);

            when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

            ProductUseCase.CreateProductCommand command =
                    new ProductUseCase.CreateProductCommand(
                            "Gaming Laptop",
                            "electronics",
                            "electronics",
                            new BigDecimal("1200.00"),
                            50
                    );

            // When
            List<ProductDtos> result =
                    productApplicationService.createProducts(List.of(command));

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(productId.toString(), result.get(0).id().toString());

            Mockito.verify(productRepository, times(1)).save(any(Product.class));
        }

        @Test
        @DisplayName("Should create multiple products successfully")
        void shouldCreateMultipleProductsSuccessfully() {
            // Given
            UUID laptopId = UUID.randomUUID();
            UUID mouseId = UUID.randomUUID();

            Product savedLaptop = new Product(laptopId, "Gaming Laptop", "electronics","electronics",
                    new BigDecimal("1200.00"), 50, 0L);
            Product savedMouse = new Product(mouseId, "Gaming Mouse", "electronics","electronics",
                    new BigDecimal("80.00"), 100, 0L);

            when(productRepository.save(any(Product.class)))
                    .thenReturn(savedLaptop)
                    .thenReturn(savedMouse);

            List<ProductUseCase.CreateProductCommand> commands = List.of(
                    new ProductUseCase.CreateProductCommand(
                            "Gaming Laptop", "electronics","electronics", new BigDecimal("1200.00"), 50),
                    new ProductUseCase.CreateProductCommand(
                            "Gaming Mouse", "electronics","electronics", new BigDecimal("80.00"), 100)
            );

            // When
            List<ProductDtos> result =
                    productApplicationService.createProducts(commands);

            // Then
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(laptopId.toString(), result.get(0).id().toString());
            assertEquals(mouseId.toString(), result.get(1).id().toString());

            Mockito.verify(productRepository, times(2)).save(any(Product.class));
        }

        @Test
        @DisplayName("Should validate product name is not null or empty")
        void shouldValidateProductNameIsNotNullOrEmpty() {
            // Given
            ProductUseCase.CreateProductCommand commandWithNullName =
                    new ProductUseCase.CreateProductCommand(
                            null, "electronics","electronics", new BigDecimal("100.00"), 10);

            ProductUseCase.CreateProductCommand commandWithEmptyName =
                    new ProductUseCase.CreateProductCommand(
                            "", "electronics","electronics", new BigDecimal("100.00"), 10);

            // When & Then
            assertThrows(IllegalArgumentException.class,
                    () -> productApplicationService.createProducts(List.of(commandWithNullName)));

            assertThrows(IllegalArgumentException.class,
                    () -> productApplicationService.createProducts(List.of(commandWithEmptyName)));

            Mockito.verify(productRepository, never()).save(any(Product.class));
        }

        @Test
        @DisplayName("Should validate product category is not null or empty")
        void shouldValidateProductCategoryIsNotNullOrEmpty() {
            // Given
            ProductUseCase.CreateProductCommand commandWithNullCategory =
                    new ProductUseCase.CreateProductCommand(
                            "Test Product", null,"", new BigDecimal("100.00"), 10);

            ProductUseCase.CreateProductCommand commandWithEmptyCategory =
                    new ProductUseCase.CreateProductCommand(
                            "Test Product", "","", new BigDecimal("100.00"), 10);

            // When & Then
            assertThrows(IllegalArgumentException.class,
                    () -> productApplicationService.createProducts(List.of(commandWithNullCategory)));

            assertThrows(IllegalArgumentException.class,
                    () -> productApplicationService.createProducts(List.of(commandWithEmptyCategory)));

            Mockito.verify(productRepository, never()).save(any(Product.class));
        }

        @Test
        @DisplayName("Should validate product price is not null or negative")
        void shouldValidateProductPriceIsNotNullOrNegative() {
            // Given
            ProductUseCase.CreateProductCommand commandWithNullPrice =
                    new ProductUseCase.CreateProductCommand(
                            "Test Product", "electronics","electronics", null, 10);

            ProductUseCase.CreateProductCommand commandWithNegativePrice =
                    new ProductUseCase.CreateProductCommand(
                            "Test Product", "electronics","electronics", new BigDecimal("-100.00"), 10);

            // When & Then
            assertThrows(IllegalArgumentException.class,
                    () -> productApplicationService.createProducts(List.of(commandWithNullPrice)));

            assertThrows(IllegalArgumentException.class,
                    () -> productApplicationService.createProducts(List.of(commandWithNegativePrice)));

            Mockito.verify(productRepository, never()).save(any(Product.class));
        }

        @Test
        @DisplayName("Should validate product stock is not negative")
        void shouldValidateProductStockIsNotNegative() {
            // Given
            ProductUseCase.CreateProductCommand commandWithNegativeStock =
                    new ProductUseCase.CreateProductCommand(
                            "Test Product", "electronics","electronics", new BigDecimal("100.00"), -10);

            // When & Then
            assertThrows(IllegalArgumentException.class,
                    () -> productApplicationService.createProducts(List.of(commandWithNegativeStock)));

            Mockito.verify(productRepository, never()).save(any(Product.class));
        }

        @Test
        @DisplayName("Should handle empty command list")
        void shouldHandleEmptyCommandList() {
            // When & Then
            assertThrows(IllegalArgumentException.class,
                    () -> productApplicationService.createProducts(List.of()));

            Mockito.verify(productRepository, never()).save(any(Product.class));
        }

        @Test
        @DisplayName("Should handle null command list")
        void shouldHandleNullCommandList() {
            // When & Then
            assertThrows(IllegalArgumentException.class,
                    () -> productApplicationService.createProducts(null));

            Mockito.verify(productRepository, never()).save(any(Product.class));
        }

        @Test
        @DisplayName("Should handle repository save failure")
        void shouldHandleRepositorySaveFailure() {
            // Given
            when(productRepository.save(any(Product.class)))
                    .thenThrow(new RuntimeException("Database error"));

            ProductUseCase.CreateProductCommand command =
                    new ProductUseCase.CreateProductCommand(
                            "Test Product", "electronics","electronics", new BigDecimal("100.00"), 10);

            // When & Then
            assertThrows(RuntimeException.class,
                    () -> productApplicationService.createProducts(List.of(command)));

            Mockito.verify(productRepository, times(1)).save(any(Product.class));
        }

        @Test
        @DisplayName("Should create product with zero stock")
        void shouldCreateProductWithZeroStock() {
            // Given
            UUID productId = UUID.randomUUID();
            Product savedProduct = new Product(productId, "Pre-order Item", "electronics","electronics",
                    new BigDecimal("999.99"), 0, 0L);

            when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

            ProductUseCase.CreateProductCommand command =
                    new ProductUseCase.CreateProductCommand(
                            "Pre-order Item", "electronics","electronics", new BigDecimal("999.99"), 0);

            // When
            List<ProductDtos> result =
                    productApplicationService.createProducts(List.of(command));

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(productId.toString(), result.get(0).id().toString());

            Mockito.verify(productRepository, times(1)).save(any(Product.class));
        }

        @Test
        @DisplayName("Should create product with decimal price")
        void shouldCreateProductWithDecimalPrice() {
            // Given
            UUID productId = UUID.randomUUID();
            Product savedProduct = new Product(productId, "Budget Mouse", "electronics","electronics",
                    new BigDecimal(19.99), 200, 0L);

            when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

            ProductUseCase.CreateProductCommand command =
                    new ProductUseCase.CreateProductCommand(
                            "Budget Mouse", "electronics","electronics", new BigDecimal("19.99"), 200);

            // When
            List<ProductDtos> result =
                    productApplicationService.createProducts(List.of(command));

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(productId.toString(), result.get(0).id().toString());

            Mockito.verify(productRepository, times(1)).save(any(Product.class));
        }

        @Test
        @DisplayName("Should create products in different categories")
        void shouldCreateProductsInDifferentCategories() {
            // Given
            UUID electronicsId = UUID.randomUUID();
            UUID furnitureId = UUID.randomUUID();

            Product savedElectronics = new Product(electronicsId, "Laptop", "electronics",
                    "electronics", new BigDecimal("1500.00"),15,0L);
            Product savedFurniture = new Product(furnitureId, "Desk Chair", "furniture","furniture",
                    new BigDecimal("300.00"),15, 0L);

            when(productRepository.save(any(Product.class)))
                    .thenReturn(savedElectronics)
                    .thenReturn(savedFurniture);

            List<ProductUseCase.CreateProductCommand> commands = List.of(
                    new ProductUseCase.CreateProductCommand(
                            "Laptop", "electronics", "electronics", new BigDecimal("1500.00"), 30),
                    new ProductUseCase.CreateProductCommand(
                            "Desk Chair", "furniture","furniture", new BigDecimal("300.00"), 15)
            );

            // When
            List<ProductDtos> result =
                    productApplicationService.createProducts(commands);

            // Then
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(electronicsId.toString(), result.get(0).id().toString());
            assertEquals(furnitureId.toString(), result.get(1).id().toString());

            Mockito.verify(productRepository, times(2)).save(any(Product.class));
        }
    }


}