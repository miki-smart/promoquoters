package com.backend.promoquoter.infrastructure.adapter.in.response;

import java.util.List;

public record CartConfirmResponseDto(
        String orderId,
        List<LineItemDto> lineItems,
        List<AppliedPromotionDto> appliedPromotions,
        Double total
) {
    public record LineItemDto(String productId, String productName, int quantity, Double unitPrice, Double lineDiscount, Double lineTotal) {}
    public record AppliedPromotionDto(String promotionId, String type, String description, Double amount, int priority) {}
}
