package com.backend.promoquoter.domain.promotion;

import com.backend.promoquoter.domain.pricing.PricingContext;

import java.util.List;

public class PromotionPipeline {
    private final List<PromotionRule> rules;

    public PromotionPipeline(List<PromotionRule> rules) {
        this.rules = rules == null ? List.of() : List.copyOf(rules);
    }

    public PricingContext execute(PricingContext context) {
        for (PromotionRule rule : rules) {
            if (rule.isApplicable(context)) {
                rule.apply(context);
            }
        }
        return context;
    }

}
