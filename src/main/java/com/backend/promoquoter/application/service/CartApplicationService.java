// src/main/java/com/backend/promoquoter/application/service/CartApplicationService.java
package com.backend.promoquoter.application.service;

import com.backend.promoquoter.application.port.in.CalculateCartQuoteUseCase;
import com.backend.promoquoter.application.port.in.ConfirmCartUseCase;
import com.backend.promoquoter.application.port.out.IOrderRepository;
import com.backend.promoquoter.application.port.out.IProductRepository;
import com.backend.promoquoter.application.port.out.IPromotionRepository;
import com.backend.promoquoter.domain.model.Order;
import com.backend.promoquoter.domain.model.Product;
import com.backend.promoquoter.domain.model.Promotion;
import com.backend.promoquoter.domain.pricing.PricingContext;
import com.backend.promoquoter.domain.promotion.PromotionEngine;
import com.backend.promoquoter.domain.promotion.PromotionRuleFactory;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CartApplicationService implements CalculateCartQuoteUseCase, ConfirmCartUseCase {

    private final IPromotionRepository promotionRepository;
    private final IProductRepository productRepository;
    private final PromotionEngine promotionEngine;
    private final IOrderRepository orderRepository;

    public CartApplicationService(IPromotionRepository promotionRepository, IProductRepository productRepository, IOrderRepository orderRepository) {
        this.promotionRepository = promotionRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.promotionEngine=new PromotionEngine(new PromotionRuleFactory());
    }
    @Override
    public CartQuoteResult calculateQuote(CalculateCartQuoteCommand command) {
        // 1. Validate input
        validateQuoteCommand(command);

        // 2. Load products
        List<UUID> productIds = command.items().stream()
                .map(item -> UUID.fromString(item.productId()))
                .toList();

        List<Product> products = productRepository.findByIds(productIds);
        Map<UUID, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        // 3. Validate all products exist
        validateQuoteProductsExist(command.items(), productMap);

        // 4. Build pricing context
        PricingContext.Builder contextBuilder = PricingContext.builder()
                .withCustomerSegment(command.customerSegment());

        for (CalculateCartQuoteUseCase.CartItemCommand item : command.items()) {
            UUID productId = UUID.fromString(item.productId());
            Product product = productMap.get(productId);

            contextBuilder.addItem(
                    productId,
                    product.getName(),
                    product.getCategory(),
                    product.getPrice(),
                    item.qty()
            );
        }

        PricingContext context = contextBuilder.build();

        // 5. Load and apply promotions
        List<Promotion> activePromotions = promotionRepository.findActiveBySegmentOrdered(command.customerSegment());
        PricingContext finalContext = promotionEngine.applyPromotions(context, activePromotions);

        // 6. Build result
        return buildQuoteResult(finalContext);
    }

    @Override
    @Transactional
    public CartConfirmResult confirmCart(ConfirmCartCommand command) {
        // 1. Check for idempotency
        if (command.idempotencyKey().isPresent()) {
            var existingOrder = orderRepository.findByIdempotencyKey(command.idempotencyKey().get());
            if (existingOrder.isPresent()) {
                return buildConfirmResultFromOrder(existingOrder.get());
            }
        }

        // 2. Validate input
        validateConfirmCommand(command);

        // 3. Load products with pessimistic locking for stock updates
        List<UUID> productIds = command.items().stream()
                .map(item -> UUID.fromString(item.productId()))
                .toList();

        List<Product> products = productRepository.findByIdsForUpdate(productIds);
        Map<UUID, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        // 4. Validate all products exist and check stock
        validateConfirmProductsExist(command.items(), productMap);
        validateStockAvailability(command.items(), productMap);

        // 5. Reserve stock
        for (ConfirmCartUseCase.CartItemCommand item : command.items()) {
            UUID productId = UUID.fromString(item.productId());
            Product product = productMap.get(productId);
            product.reserveStock(item.qty());
        }

        // 6. Calculate final pricing (same as quote)
        PricingContext.Builder contextBuilder = PricingContext.builder()
                .withCustomerSegment(command.customerSegment());

        for (ConfirmCartUseCase.CartItemCommand item : command.items()) {
            UUID productId = UUID.fromString(item.productId());
            Product product = productMap.get(productId);

            contextBuilder.addItem(
                    productId,
                    product.getName(),
                    product.getCategory(),
                    product.getPrice(),
                    item.qty()
            );
        }

        PricingContext context = contextBuilder.build();

        // 7. Apply promotions
        List<Promotion> activePromotions = promotionRepository.findActiveBySegmentOrdered(command.customerSegment());
        PricingContext finalContext = promotionEngine.applyPromotions(context, activePromotions);

        // 8. Create and save order
        Order order = Order.createFromPricingContext(
                finalContext,
                command.customerSegment(),
                command.idempotencyKey().orElse(null)
        );

        Order savedOrder = orderRepository.save(order);

        // 9. Save updated product stock
        productRepository.saveAll(products);

        // 10. Build result
        return buildConfirmResultFromOrder(savedOrder);
    }

    private void validateQuoteCommand(CalculateCartQuoteCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Command cannot be null");
        }
        if (command.items() == null || command.items().isEmpty()) {
            throw new IllegalArgumentException("Cart items cannot be null or empty");
        }
        for (CalculateCartQuoteUseCase.CartItemCommand item : command.items()) {
            if (item.productId() == null || item.productId().trim().isEmpty()) {
                throw new IllegalArgumentException("Product ID cannot be null or empty");
            }
            if (item.qty() <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than 0");
            }
            try {
                UUID.fromString(item.productId());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid product ID format: " + item.productId());
            }
        }
    }

    private void validateConfirmCommand(ConfirmCartCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Command cannot be null");
        }
        if (command.items() == null || command.items().isEmpty()) {
            throw new IllegalArgumentException("Cart items cannot be null or empty");
        }
        for (ConfirmCartUseCase.CartItemCommand item : command.items()) {
            if (item.productId() == null || item.productId().trim().isEmpty()) {
                throw new IllegalArgumentException("Product ID cannot be null or empty");
            }
            if (item.qty() <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than 0");
            }
            try {
                UUID.fromString(item.productId());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid product ID format: " + item.productId());
            }
        }
    }

    private void validateQuoteProductsExist(List<CalculateCartQuoteUseCase.CartItemCommand> items, Map<UUID, Product> productMap) {
        for (CalculateCartQuoteUseCase.CartItemCommand item : items) {
            UUID productId = UUID.fromString(item.productId());
            if (!productMap.containsKey(productId)) {
                throw new ProductNotFoundException("Product not found: " + item.productId());
            }
        }
    }

    private void validateConfirmProductsExist(List<ConfirmCartUseCase.CartItemCommand> items, Map<UUID, Product> productMap) {
        for (ConfirmCartUseCase.CartItemCommand item : items) {
            UUID productId = UUID.fromString(item.productId());
            if (!productMap.containsKey(productId)) {
                throw new ProductNotFoundException("Product not found: " + item.productId());
            }
        }
    }

    private void validateStockAvailability(List<ConfirmCartUseCase.CartItemCommand> items, Map<UUID, Product> productMap) {
        for (ConfirmCartUseCase.CartItemCommand item : items) {
            UUID productId = UUID.fromString(item.productId());
            Product product = productMap.get(productId);
            if (!product.isAvailable(item.qty())) {
                throw new InsufficientStockException(
                        String.format("Insufficient stock for product %s. Requested: %d, Available: %d",
                                item.productId(), item.qty(), product.getStock())
                );
            }
        }
    }

    private CartQuoteResult buildQuoteResult(PricingContext context) {
        String quoteId = UUID.randomUUID().toString();

        List<CalculateCartQuoteUseCase.LineItemResult> lineItems = context.getItems().stream()
                .map(item -> new CalculateCartQuoteUseCase.LineItemResult(
                        item.getProductId().toString(),
                        item.getName(),
                        item.getQty(),
                        item.getUnitPrice().doubleValue(),
                        item.getDiscount().doubleValue(),
                        item.getLineTotal().doubleValue()
                ))
                .toList();

        List<CalculateCartQuoteUseCase.AppliedPromotionResult> appliedPromotions = context.getAppliedPromotions().stream()
                .map(promo -> new CalculateCartQuoteUseCase.AppliedPromotionResult(
                        promo.promotionId().toString(),
                        promo.type(),
                        promo.description(),
                        promo.amount().doubleValue(),
                        promo.priority()
                ))
                .toList();

        return new CartQuoteResult(
                quoteId,
                lineItems,
                appliedPromotions,
                context.getSubtotal().doubleValue(),
                context.getTotalDiscount().doubleValue(),
                context.getTotal().doubleValue()
        );
    }

    private CartConfirmResult buildConfirmResultFromOrder(Order order) {
        List<ConfirmCartUseCase.LineItemResult> lineItems = order.getItems().stream()
                .map(item -> new ConfirmCartUseCase.LineItemResult(
                        item.getProductId().toString(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getUnitPrice().doubleValue(),
                        item.getLineDiscount().doubleValue(),
                        item.getLineTotal().doubleValue()
                ))
                .toList();

        List<ConfirmCartUseCase.AppliedPromotionResult> appliedPromotions = order.getAppliedPromotions().stream()
                .map(promo -> new ConfirmCartUseCase.AppliedPromotionResult(
                        promo.getPromotionId().toString(),
                        promo.getType(),
                        promo.getDescription(),
                        promo.getAmount().doubleValue(),
                        promo.getPriority()
                ))
                .toList();

        return new CartConfirmResult(
                order.getId().toString(),
                lineItems,
                appliedPromotions,
                order.getSubtotal().doubleValue(),
                order.getTotalDiscount().doubleValue(),
                order.getTotal().doubleValue(),
                order.getCreatedAt(),
                order.getStatus().toString()
        );
    }

    // Custom exceptions
    public static class ProductNotFoundException extends RuntimeException {
        public ProductNotFoundException(String message) {
            super(message);
        }
    }

    public static class InsufficientStockException extends RuntimeException {
        public InsufficientStockException(String message) {
            super(message);
        }
    }
}