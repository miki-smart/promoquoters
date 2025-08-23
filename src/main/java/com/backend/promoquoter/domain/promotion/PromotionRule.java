package com.backend.promoquoter.domain.promotion;

import com.backend.promoquoter.domain.pricing.PricingContext;

public interface PromotionRule {
    String type();
    int priority();
    boolean isApplicable(PricingContext context);
    void apply(PricingContext context);
}
