package com.backend.promoquoter.application.port.in;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ConfirmCartUseCase {
    CartConfirmResult confirmCart(ConfirmCartCommand command);

    record ConfirmCartCommand(
        List<CartItemCommand> items, 
        String customerSegment, 
        Optional<String> idempotencyKey
    ) {}

    record CartItemCommand(
        String productId, 
        int qty
    ) {}

    record CartConfirmResult(
        String orderId,
        List<LineItemResult> lineItems,
        List<AppliedPromotionResult> appliedPromotions,
        double subtotal,
        double totalDiscount,
        double total,
        LocalDateTime confirmedAt,
        String status
    ) {}

    record LineItemResult(
        String productId,
        String productName,
        int quantity,
        double unitPrice,
        double lineDiscount,
        double lineTotal
    ) {}

    record AppliedPromotionResult(
        String promotionId,
        String type,
        String description,
        double amount,
        int priority
    ) {}
}
