package com.backend.promoquoter.domain.promotion;


import com.backend.promoquoter.domain.model.Promotion;
import com.backend.promoquoter.domain.promotion.rule.BuyXGetYRule;
import com.backend.promoquoter.domain.promotion.rule.PercentOffCategoryRule;

import java.math.BigDecimal;
import java.util.*;

public class PromotionRuleFactory {

    public List<PromotionRule> buildRules(List<Promotion> defs) {
        if (defs == null || defs.isEmpty()) return List.of();
        List<PromotionRule> rules = new ArrayList<>();

        for (Promotion p : defs) {
            if (!p.isActive()) continue;
            String type = p.getType();
            Map<String, Object> cfg = p.getConfig() == null ? Map.of() : p.getConfig();

            switch (type) {
                case "PERCENT_OFF_CATEGORY" -> rules.add(
                        new PercentOffCategoryRule(
                                p.getId(),
                                string(cfg.get("category")),
                                bigDecimal(cfg.get("percent")),
                                p.getPriority()
                        )
                );
                case "BUY_X_GET_Y" -> rules.add(
                        new BuyXGetYRule(
                                p.getId(),
                                uuid(cfg.get("productId")),
                                integer(cfg.get("buy")),
                                integer(cfg.get("free")),
                                p.getPriority()
                        )
                );
                default -> { /* ignore unknown types for now */ }
            }
        }

        // Stable sort by priority (ascending)
        rules.sort(Comparator.comparingInt(PromotionRule::priority));
        return rules;
    }

    private static String string(Object v) { return v == null ? null : String.valueOf(v); }
    private static BigDecimal bigDecimal(Object v) {
        if (v == null) return BigDecimal.ZERO;
        if (v instanceof BigDecimal bd) return bd;
        return new BigDecimal(String.valueOf(v));
    }
    private static int integer(Object v) {
        if (v == null) return 0;
        if (v instanceof Number n) return n.intValue();
        return Integer.parseInt(String.valueOf(v));
    }
    private static UUID uuid(Object v) {
        if (v == null) return null;
        if (v instanceof UUID u) return u;
        return UUID.fromString(String.valueOf(v));
    }
}
