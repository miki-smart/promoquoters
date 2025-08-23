package com.backend.promoquoter.infrastructure.adapter.in.response;

import java.util.List;

public record CartQuoteResponseDto(
        String quoteId,
        List<LineItemDto> lineItems,
        List<AppliedPromotionDto> appliedPromotions,
        Double subtotal,
        Double totalDiscount,
        Double total
) {
    public record LineItemDto(String productId, String productName, int quantity, Double unitPrice, Double lineDiscount, Double lineTotal) {}
    public record AppliedPromotionDto(String promotionId, String type, String description, Double amount, int priority) {}
}
