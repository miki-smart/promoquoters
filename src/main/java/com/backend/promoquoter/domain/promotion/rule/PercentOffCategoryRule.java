package com.backend.promoquoter.domain.promotion.rule;


import com.backend.promoquoter.domain.pricing.PricingContext;
import com.backend.promoquoter.domain.promotion.PromotionRule;

import java.math.BigDecimal;
import java.util.UUID;

public class PercentOffCategoryRule implements PromotionRule {
    private final UUID promotionId;
    private final String targetCategory;
    private final BigDecimal percent; // e.g., 10 => 10%
    private final int priority;

    public PercentOffCategoryRule(UUID promotionId, String targetCategory, BigDecimal percent, int priority) {
        this.promotionId = promotionId;
        this.targetCategory = targetCategory;
        this.percent = percent == null ? BigDecimal.ZERO : percent;
        this.priority = priority;
    }

    @Override public String type() { return "PERCENT_OFF_CATEGORY"; }
    @Override public int priority() { return priority; }

    @Override
    public boolean isApplicable(PricingContext context) {
        if (percent.compareTo(BigDecimal.ZERO) <= 0) return false;
        return context.getItems().stream().anyMatch(i -> targetCategory.equalsIgnoreCase(i.getCategory()));
    }

    @Override
    public void apply(PricingContext context) {
        if (!isApplicable(context)) return;
        BigDecimal totalDiscount = BigDecimal.ZERO;

        for (PricingContext.LineItem item : context.getItems()) {
            if (!targetCategory.equalsIgnoreCase(item.getCategory())) continue;
            BigDecimal itemDiscount = item.getSubtotal()
                    .multiply(percent)
                    .divide(BigDecimal.valueOf(100), 2, PricingContext.ROUNDING);
            item.addDiscount(itemDiscount);
            totalDiscount = totalDiscount.add(itemDiscount);
        }

        if (totalDiscount.compareTo(BigDecimal.ZERO) > 0) {
            context.recordPromotion(new PricingContext.AppliedPromotion(
                    promotionId, type(),
                    percent.stripTrailingZeros().toPlainString() + "% off " + targetCategory,
                    totalDiscount, priority));
        }
    }
}
