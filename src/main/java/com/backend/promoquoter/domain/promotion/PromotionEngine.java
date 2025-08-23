package com.backend.promoquoter.domain.promotion;

import com.backend.promoquoter.domain.model.Promotion;
import com.backend.promoquoter.domain.pricing.PricingContext;

import java.util.List;

public class PromotionEngine {
    private final PromotionRuleFactory factory;

    public PromotionEngine(PromotionRuleFactory factory) {
        this.factory = factory;
    }

    public PricingContext applyPromotions(PricingContext context, List<Promotion> definitions) {
        List<PromotionRule> rules = factory.buildRules(definitions);
        PromotionPipeline pipeline = new PromotionPipeline(rules);
        return pipeline.execute(context);
    }
}
