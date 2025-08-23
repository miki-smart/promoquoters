package com.backend.promoquoter.domain.promotion.rule;


import com.backend.promoquoter.domain.pricing.PricingContext;
import com.backend.promoquoter.domain.promotion.PromotionRule;

import java.math.BigDecimal;
import java.util.UUID;

public class BuyXGetYRule implements PromotionRule {
    private final UUID promotionId;
    private final UUID productId;
    private final int buyQty;
    private final int freeQty;
    private final int priority;

    public BuyXGetYRule(UUID promotionId, UUID productId, int buyQty, int freeQty, int priority) {
        this.promotionId = promotionId;
        this.productId = productId;
        this.buyQty = buyQty;
        this.freeQty = freeQty;
        this.priority = priority;
    }

    @Override public String type() { return "BUY_X_GET_Y"; }
    @Override public int priority() { return priority; }

    @Override
    public boolean isApplicable(PricingContext context) {
        if (buyQty <= 0 || freeQty <= 0) return false;
        return context.getItems().stream().anyMatch(i -> productId.equals(i.getProductId()));
    }

    @Override
    public void apply(PricingContext context) {
        if (!isApplicable(context)) return;
        BigDecimal totalDiscount = BigDecimal.ZERO;

        for (PricingContext.LineItem item : context.getItems()) {
            if (!productId.equals(item.getProductId())) continue;
            int qty = item.getQty();
            if (qty < buyQty) continue;

            // Simple interpretation: for each group of (buyQty + freeQty), Y are free.
            int group = buyQty + freeQty;
            int eligibleFree = (group > 0) ? (qty / group) * freeQty : 0;

            if (eligibleFree > 0) {
                BigDecimal lineDiscount = item.getUnitPrice()
                        .multiply(BigDecimal.valueOf(eligibleFree))
                        .setScale(2, PricingContext.ROUNDING);
                item.addDiscount(lineDiscount);
                totalDiscount = totalDiscount.add(lineDiscount);
            }
        }

        if (totalDiscount.compareTo(BigDecimal.ZERO) > 0) {
            context.recordPromotion(new PricingContext.AppliedPromotion(
                    promotionId, type(),
                    "Buy " + buyQty + " get " + freeQty + " free",
                    totalDiscount, priority));
        }
    }
}