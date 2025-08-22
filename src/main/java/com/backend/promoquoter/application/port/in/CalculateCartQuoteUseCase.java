package com.backend.promoquoter.application.port.in;

import java.util.List;

public interface CalculateCartQuoteUseCase {
    CartQuoteResult calculateQuote(CalculateCartQuoteCommand command);

    record CalculateCartQuoteCommand(
            List<CartItemCommand> items,
            String customerSegment
    ) {}

    record CartItemCommand(
            String productId,
            int qty
    ) {}

    record CartQuoteResult(
            String quoteId,
            List<LineItemResult> lineItems,
            List<AppliedPromotionResult> appliedPromotions,
            double subtotal,
            double totalDiscount,
            double total
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
