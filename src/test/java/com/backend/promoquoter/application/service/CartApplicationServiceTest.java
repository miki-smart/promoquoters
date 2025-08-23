package com.backend.promoquoter.application.service;

import com.backend.promoquoter.application.port.in.CalculateCartQuoteUseCase;
import com.backend.promoquoter.application.port.out.IProductRepository;
import com.backend.promoquoter.application.port.out.IPromotionRepository;
import com.backend.promoquoter.domain.model.Product;
import com.backend.promoquoter.domain.model.Promotion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CartApplicationService Tests")
class CartApplicationServiceTest {

    @Mock
    private IProductRepository productRepository;

    @Mock
    private IPromotionRepository promotionRepository;

    private CartApplicationService cartApplicationService;

    private Product testProduct1;
    private Product testProduct2;
    private Product testProduct3;
    private Promotion testPromotion1;
    private Promotion testPromotion2;
    private UUID product1Id;
    private UUID product2Id;
    private UUID product3Id;

    @BeforeEach
    void setUp() {
        cartApplicationService = new CartApplicationService(promotionRepository, productRepository);

        product1Id = UUID.randomUUID();
        product2Id = UUID.randomUUID();
        product3Id = UUID.randomUUID();

        testProduct1 = new Product(
                product1Id,
                "Gaming Laptop",
                "electronics",
                "electronics",
                new BigDecimal("1200.00"),
                50,
                0L
        );

        testProduct2 = new Product(
                product2Id,
                "Gaming Mouse",
                "electronics",
                "electronics",
                new BigDecimal("80.00"),
                100,
                0L
        );

        testProduct3 = new Product(
                product3Id,
                "Office Chair",
                "furniture",
                "furniture",
                new BigDecimal("200.00"),
                20,
                0L
        );

        testPromotion1 = new Promotion(
                UUID.randomUUID(),
                "PERCENT_OFF_CATEGORY",
                Map.of("percent", "10", "category", "electronics"),
                1,
                true
        );

        testPromotion2 = new Promotion(
                UUID.randomUUID(),
                "BUY_X_GET_Y",
                Map.of("productId", product2Id.toString(), "buy", "3", "free", "1"),
                2,
                true
        );
    }

    @Nested
    @DisplayName("Calculate Cart Quote Tests")
    class CalculateCartQuoteTests {

        @Test
        @DisplayName("Should calculate quote for single item without promotions")
        void shouldCalculateQuoteForSingleItemWithoutPromotions() {
            // Given
            var command = new CalculateCartQuoteUseCase.CalculateCartQuoteCommand(
                    List.of(new CalculateCartQuoteUseCase.CartItemCommand(product1Id.toString(), 1)),
                    "standard"
            );

            when(productRepository.findByIds(List.of(product1Id))).thenReturn(List.of(testProduct1));
            when(promotionRepository.findActiveBySegmentOrdered("standard")).thenReturn(List.of());

            // When
            CalculateCartQuoteUseCase.CartQuoteResult result = cartApplicationService.calculateQuote(command);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.quoteId()).isNotNull();
            assertThat(result.lineItems()).hasSize(1);
            assertThat(result.subtotal()).isEqualTo(1200.0);
            assertThat(result.totalDiscount()).isEqualTo(0.0);
            assertThat(result.total()).isEqualTo(1200.0);
            assertThat(result.appliedPromotions()).isEmpty();

            var lineItem = result.lineItems().get(0);
            assertThat(lineItem.productId()).isEqualTo(product1Id.toString());
            assertThat(lineItem.productName()).isEqualTo("Gaming Laptop");
            assertThat(lineItem.quantity()).isEqualTo(1);
            assertThat(lineItem.unitPrice()).isEqualTo(1200.0);
            assertThat(lineItem.lineDiscount()).isEqualTo(0.0);
            assertThat(lineItem.lineTotal()).isEqualTo(1200.0);

            verify(productRepository).findByIds(List.of(product1Id));
            verify(promotionRepository).findActiveBySegmentOrdered("standard");
        }

        @Test
        @DisplayName("Should calculate quote for multiple items with promotions")
        void shouldCalculateQuoteForMultipleItemsWithPromotions() {
            // Given
            var command = new CalculateCartQuoteUseCase.CalculateCartQuoteCommand(
                    List.of(
                            new CalculateCartQuoteUseCase.CartItemCommand(product1Id.toString(), 1),
                            new CalculateCartQuoteUseCase.CartItemCommand(product2Id.toString(), 2)
                    ),
                    "premium"
            );

            when(productRepository.findByIds(List.of(product1Id, product2Id)))
                    .thenReturn(List.of(testProduct1, testProduct2));
            when(promotionRepository.findActiveBySegmentOrdered("premium"))
                    .thenReturn(List.of(testPromotion1));

            // When
            CalculateCartQuoteUseCase.CartQuoteResult result = cartApplicationService.calculateQuote(command);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.lineItems()).hasSize(2);
            // Subtotal: 1200 + (80 * 2) = 1360
            assertThat(result.subtotal()).isEqualTo(1360.0);
            // Should have some discount for electronics items
            assertThat(result.totalDiscount()).isGreaterThan(0);
            assertThat(result.total()).isLessThan(result.subtotal());

            verify(productRepository).findByIds(List.of(product1Id, product2Id));
            verify(promotionRepository).findActiveBySegmentOrdered("premium");
        }

        @Test
        @DisplayName("Should handle empty cart")
        void shouldHandleEmptyCart() {
            // Given
            var command = new CalculateCartQuoteUseCase.CalculateCartQuoteCommand(
                    List.of(),
                    "standard"
            );

            // When & Then
            assertThatThrownBy(() -> cartApplicationService.calculateQuote(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Cart items cannot be null or empty");

            verifyNoInteractions(productRepository, promotionRepository);
        }

        @Test
        @DisplayName("Should handle single item with large qty")
        void shouldHandleSingleItemWithLargeqty() {
            // Given
            var command = new CalculateCartQuoteUseCase.CalculateCartQuoteCommand(
                    List.of(new CalculateCartQuoteUseCase.CartItemCommand(product2Id.toString(), 5)),
                    "standard"
            );

            when(productRepository.findByIds(List.of(product2Id))).thenReturn(List.of(testProduct2));
            when(promotionRepository.findActiveBySegmentOrdered("standard")).thenReturn(List.of());

            // When
            CalculateCartQuoteUseCase.CartQuoteResult result = cartApplicationService.calculateQuote(command);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.lineItems()).hasSize(1);
            assertThat(result.subtotal()).isEqualTo(400.0); // 80 * 5
            assertThat(result.total()).isEqualTo(400.0);

            var lineItem = result.lineItems().get(0);
            assertThat(lineItem.quantity()).isEqualTo(5);
            assertThat(lineItem.lineTotal()).isEqualTo(400.0);
        }

        @Test
        @DisplayName("Should handle mixed category cart with category-specific promotions")
        void shouldHandleMixedCategoryCartWithPromotions() {
            // Given
            var command = new CalculateCartQuoteUseCase.CalculateCartQuoteCommand(
                    List.of(
                            new CalculateCartQuoteUseCase.CartItemCommand(product1Id.toString(), 1), // electronics
                            new CalculateCartQuoteUseCase.CartItemCommand(product3Id.toString(), 1)  // furniture
                    ),
                    "premium"
            );

            when(productRepository.findByIds(List.of(product1Id, product3Id)))
                    .thenReturn(List.of(testProduct1, testProduct3));
            when(promotionRepository.findActiveBySegmentOrdered("premium"))
                    .thenReturn(List.of(testPromotion1)); // electronics only

            // When
            CalculateCartQuoteUseCase.CartQuoteResult result = cartApplicationService.calculateQuote(command);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.lineItems()).hasSize(2);
            // Subtotal: 1200 + 200 = 1400
            assertThat(result.subtotal()).isEqualTo(1400.0);
            // Only electronics should get discount, not furniture
            assertThat(result.total()).isLessThan(result.subtotal());
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Should throw exception for null command")
        void shouldThrowExceptionForNullCommand() {
            // When & Then
            assertThatThrownBy(() -> cartApplicationService.calculateQuote(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Command cannot be null");

            verifyNoInteractions(productRepository, promotionRepository);
        }

        @Test
        @DisplayName("Should throw exception for null items")
        void shouldThrowExceptionForNullItems() {
            // Given
            var command = new CalculateCartQuoteUseCase.CalculateCartQuoteCommand(null, "standard");

            // When & Then
            assertThatThrownBy(() -> cartApplicationService.calculateQuote(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Cart items cannot be null or empty");

            verifyNoInteractions(productRepository, promotionRepository);
        }

        @Test
        @DisplayName("Should throw exception for zero qty")
        void shouldThrowExceptionForZeroqty() {
            // Given
            var command = new CalculateCartQuoteUseCase.CalculateCartQuoteCommand(
                    List.of(new CalculateCartQuoteUseCase.CartItemCommand(product1Id.toString(), 0)),
                    "standard"
            );

            // When & Then
            assertThatThrownBy(() -> cartApplicationService.calculateQuote(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("qty must be greater than 0");

            verifyNoInteractions(productRepository, promotionRepository);
        }

        @Test
        @DisplayName("Should throw exception for negative qty")
        void shouldThrowExceptionForNegativeqty() {
            // Given
            var command = new CalculateCartQuoteUseCase.CalculateCartQuoteCommand(
                    List.of(new CalculateCartQuoteUseCase.CartItemCommand(product1Id.toString(), -1)),
                    "standard"
            );

            // When & Then
            assertThatThrownBy(() -> cartApplicationService.calculateQuote(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("qty must be greater than 0");

            verifyNoInteractions(productRepository, promotionRepository);
        }

        @Test
        @DisplayName("Should throw exception for null product ID")
        void shouldThrowExceptionForNullProductId() {
            // Given
            var command = new CalculateCartQuoteUseCase.CalculateCartQuoteCommand(
                    List.of(new CalculateCartQuoteUseCase.CartItemCommand(null, 1)),
                    "standard"
            );

            // When & Then
            assertThatThrownBy(() -> cartApplicationService.calculateQuote(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Product ID cannot be null or empty");

            verifyNoInteractions(productRepository, promotionRepository);
        }

        @Test
        @DisplayName("Should throw exception for empty product ID")
        void shouldThrowExceptionForEmptyProductId() {
            // Given
            var command = new CalculateCartQuoteUseCase.CalculateCartQuoteCommand(
                    List.of(new CalculateCartQuoteUseCase.CartItemCommand("", 1)),
                    "standard"
            );

            // When & Then
            assertThatThrownBy(() -> cartApplicationService.calculateQuote(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Product ID cannot be null or empty");

            verifyNoInteractions(productRepository, promotionRepository);
        }

        @Test
        @DisplayName("Should throw exception for invalid UUID format")
        void shouldThrowExceptionForInvalidUuidFormat() {
            // Given
            var command = new CalculateCartQuoteUseCase.CalculateCartQuoteCommand(
                    List.of(new CalculateCartQuoteUseCase.CartItemCommand("invalid-uuid", 1)),
                    "standard"
            );

            // When & Then
            assertThatThrownBy(() -> cartApplicationService.calculateQuote(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid product ID format: invalid-uuid");

            verifyNoInteractions(productRepository, promotionRepository);
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should throw exception when product not found")
        void shouldThrowExceptionWhenProductNotFound() {
            // Given
            var nonExistentId = UUID.randomUUID();
            var command = new CalculateCartQuoteUseCase.CalculateCartQuoteCommand(
                    List.of(new CalculateCartQuoteUseCase.CartItemCommand(nonExistentId.toString(), 1)),
                    "standard"
            );

            when(productRepository.findByIds(List.of(nonExistentId))).thenReturn(List.of());

            // When & Then
            assertThatThrownBy(() -> cartApplicationService.calculateQuote(command)).hasMessageContaining(nonExistentId.toString());
            verify(productRepository).findByIds(List.of(nonExistentId));
            verifyNoInteractions(promotionRepository);
        }

        @Test
        @DisplayName("Should throw exception when some products not found")
        void shouldThrowExceptionWhenSomeProductsNotFound() {
            // Given
            var nonExistentId = UUID.randomUUID();
            var command = new CalculateCartQuoteUseCase.CalculateCartQuoteCommand(
                    List.of(
                            new CalculateCartQuoteUseCase.CartItemCommand(product1Id.toString(), 1),
                            new CalculateCartQuoteUseCase.CartItemCommand(nonExistentId.toString(), 1)
                    ),
                    "standard"
            );

            // Only return one product, missing the second
            when(productRepository.findByIds(List.of(product1Id, nonExistentId)))
                    .thenReturn(List.of(testProduct1));

            // When & Then
            assertThatThrownBy(() -> cartApplicationService.calculateQuote(command))
                    .isInstanceOf(CartApplicationService.ProductNotFoundException.class)
                    .hasMessageContaining("Product not found: " + nonExistentId.toString());

            verify(productRepository).findByIds(List.of(product1Id, nonExistentId));
            verifyNoInteractions(promotionRepository);
        }

        @Test
        @DisplayName("Should handle repository exceptions gracefully")
        void shouldHandleRepositoryExceptionsGracefully() {
            // Given
            var command = new CalculateCartQuoteUseCase.CalculateCartQuoteCommand(
                    List.of(new CalculateCartQuoteUseCase.CartItemCommand(product1Id.toString(), 1)),
                    "standard"
            );

            when(productRepository.findByIds(List.of(product1Id)))
                    .thenThrow(new RuntimeException("Database connection error"));

            // When & Then
            assertThatThrownBy(() -> cartApplicationService.calculateQuote(command))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Database connection error");

            verify(productRepository).findByIds(List.of(product1Id));
            verifyNoInteractions(promotionRepository);
        }

        @Test
        @DisplayName("Should handle promotion repository exceptions gracefully")
        void shouldHandlePromotionRepositoryExceptionsGracefully() {
            // Given
            var command = new CalculateCartQuoteUseCase.CalculateCartQuoteCommand(
                    List.of(new CalculateCartQuoteUseCase.CartItemCommand(product1Id.toString(), 1)),
                    "standard"
            );

            when(productRepository.findByIds(List.of(product1Id))).thenReturn(List.of(testProduct1));
            when(promotionRepository.findActiveBySegmentOrdered("standard"))
                    .thenThrow(new RuntimeException("Promotion service unavailable"));

            // When & Then
            assertThatThrownBy(() -> cartApplicationService.calculateQuote(command))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Promotion service unavailable");

            verify(productRepository).findByIds(List.of(product1Id));
            verify(promotionRepository).findActiveBySegmentOrdered("standard");
        }
    }

    @Nested
    @DisplayName("Quote ID Generation Tests")
    class QuoteIdGenerationTests {

        @Test
        @DisplayName("Should generate unique quote IDs for each request")
        void shouldGenerateUniqueQuoteIdsForEachRequest() {
            // Given
            var command = new CalculateCartQuoteUseCase.CalculateCartQuoteCommand(
                    List.of(new CalculateCartQuoteUseCase.CartItemCommand(product1Id.toString(), 1)),
                    "standard"
            );

            when(productRepository.findByIds(List.of(product1Id))).thenReturn(List.of(testProduct1));
            when(promotionRepository.findActiveBySegmentOrdered("standard")).thenReturn(List.of());

            // When
            CalculateCartQuoteUseCase.CartQuoteResult result1 = cartApplicationService.calculateQuote(command);
            CalculateCartQuoteUseCase.CartQuoteResult result2 = cartApplicationService.calculateQuote(command);

            // Then
            assertThat(result1.quoteId()).isNotNull();
            assertThat(result2.quoteId()).isNotNull();
            assertThat(result1.quoteId()).isNotEqualTo(result2.quoteId());

            // Should be valid UUIDs
            assertThatCode(() -> UUID.fromString(result1.quoteId())).doesNotThrowAnyException();
            assertThatCode(() -> UUID.fromString(result2.quoteId())).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle large quantities")
        void shouldHandleLargeQuantities() {
            // Given
            var command = new CalculateCartQuoteUseCase.CalculateCartQuoteCommand(
                    List.of(new CalculateCartQuoteUseCase.CartItemCommand(product2Id.toString(), 100)),
                    "standard"
            );

            when(productRepository.findByIds(List.of(product2Id))).thenReturn(List.of(testProduct2));
            when(promotionRepository.findActiveBySegmentOrdered("standard")).thenReturn(List.of());

            // When
            CalculateCartQuoteUseCase.CartQuoteResult result = cartApplicationService.calculateQuote(command);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.lineItems()).hasSize(1);
            assertThat(result.subtotal()).isEqualTo(8000.0); // 80 * 100
            assertThat(result.total()).isEqualTo(8000.0);

            var lineItem = result.lineItems().get(0);
            assertThat(lineItem.quantity()).isEqualTo(100);
            assertThat(lineItem.lineTotal()).isEqualTo(8000.0);
        }

        @Test
        @DisplayName("Should handle very small prices")
        void shouldHandleVerySmallPrices() {
            // Given
            var cheapProduct = new Product(
                    UUID.randomUUID(),
                    "Sticker",
                    "accessories",
                    "accessories",
                    new BigDecimal("0.01"),
                    1000,
                    0L
            );

            var command = new CalculateCartQuoteUseCase.CalculateCartQuoteCommand(
                    List.of(new CalculateCartQuoteUseCase.CartItemCommand(cheapProduct.getId().toString(), 1)),
                    "standard"
            );

            when(productRepository.findByIds(List.of(cheapProduct.getId()))).thenReturn(List.of(cheapProduct));
            when(promotionRepository.findActiveBySegmentOrdered("standard")).thenReturn(List.of());

            // When
            CalculateCartQuoteUseCase.CartQuoteResult result = cartApplicationService.calculateQuote(command);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.lineItems()).hasSize(1);
            assertThat(result.subtotal()).isEqualTo(0.01);
            assertThat(result.total()).isEqualTo(0.01);
        }

        @Test
        @DisplayName("Should handle multiple promotions with different priorities")
        void shouldHandleMultiplePromotionsWithDifferentPriorities() {
            // Given
            var command = new CalculateCartQuoteUseCase.CalculateCartQuoteCommand(
                    List.of(new CalculateCartQuoteUseCase.CartItemCommand(product1Id.toString(), 1)),
                    "vip"
            );

            when(productRepository.findByIds(List.of(product1Id))).thenReturn(List.of(testProduct1));
            when(promotionRepository.findActiveBySegmentOrdered("vip"))
                    .thenReturn(List.of(testPromotion1, testPromotion2)); // Different priorities

            // When
            CalculateCartQuoteUseCase.CartQuoteResult result = cartApplicationService.calculateQuote(command);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.subtotal()).isEqualTo(1200.0);
            // Should have some discount applied
            assertThat(result.totalDiscount()).isGreaterThan(0);
            assertThat(result.total()).isLessThan(result.subtotal());
        }
    }
}
