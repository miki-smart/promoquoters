package com.backend.promoquoter.domain.pricing;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class PricingContext {
    public static final RoundingMode ROUNDING = RoundingMode.HALF_UP;

    private final List<LineItem> items;
    private final String customerSegment;
    private final List<AppliedPromotion> appliedPromotions;
    private final Map<String, Object> metadata;

    private PricingContext(Builder b) {
        this.items = Collections.unmodifiableList(new ArrayList<>(b.items));
        this.customerSegment = b.customerSegment;
        this.appliedPromotions = new ArrayList<>();
        this.metadata = new HashMap<>(b.metadata);
    }

    public List<LineItem> getItems() { return items; }
    public String getCustomerSegment() { return customerSegment; }
    public List<AppliedPromotion> getAppliedPromotions() { return Collections.unmodifiableList(appliedPromotions); }
    public Map<String, Object> getMetadata() { return Collections.unmodifiableMap(metadata); }

    public BigDecimal getSubtotal() {
        return items.stream().map(LineItem::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, ROUNDING);
    }

    public BigDecimal getTotalDiscount() {
        return items.stream().map(LineItem::getDiscount).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, ROUNDING);
    }

    public BigDecimal getTotal() {
        BigDecimal total = getSubtotal().subtract(getTotalDiscount());
        if (total.compareTo(BigDecimal.ZERO) < 0) total = BigDecimal.ZERO;
        return total.setScale(2, ROUNDING);
    }

    public void recordPromotion(AppliedPromotion promotion) {
        this.appliedPromotions.add(promotion);
    }

    // Builder for context
    public static class Builder {
        private final List<LineItem> items = new ArrayList<>();
        private String customerSegment = "REGULAR";
        private final Map<String, Object> metadata = new HashMap<>();

        public Builder withCustomerSegment(String segment) {
            this.customerSegment = segment == null ? "REGULAR" : segment;
            return this;
        }

        public Builder addItem(UUID productId, String name, String category, BigDecimal unitPrice, int qty) {
            Objects.requireNonNull(productId, "productId");
            Objects.requireNonNull(unitPrice, "unitPrice");
            if (unitPrice.signum() < 0) throw new IllegalArgumentException("unitPrice must be >= 0.00");
            if (qty <= 0) throw new IllegalArgumentException("qty must be > 0");
            items.add(new LineItem(productId, name, category, unitPrice, qty));
            return this;
        }

        public Builder putMetadata(String key, Object value) {
            metadata.put(key, value);
            return this;
        }

        public PricingContext build() {
            if (items.isEmpty()) throw new IllegalStateException("No items");
            return new PricingContext(this);
        }
    }

    public static Builder builder() { return new Builder(); }

    // Value objects
    public static class LineItem {
        private final UUID productId;
        private final String name;
        private final String category;
        private final BigDecimal unitPrice;
        private final int qty;
        private BigDecimal discount = BigDecimal.ZERO;

        public LineItem(UUID productId, String name, String category, BigDecimal unitPrice, int qty) {
            this.productId = productId;
            this.name = name;
            this.category = category;
            if (unitPrice == null || unitPrice.signum() < 0) throw new IllegalArgumentException("unitPrice must be >= 0.00");
            this.unitPrice = unitPrice.setScale(2, ROUNDING);
            this.qty = qty;
        }

        public UUID getProductId() { return productId; }
        public String getName() { return name; }
        public String getCategory() { return category; }
        public BigDecimal getUnitPrice() { return unitPrice; }
        public int getQty() { return qty; }

        public BigDecimal getSubtotal() {
            return unitPrice.multiply(BigDecimal.valueOf(qty)).setScale(2, ROUNDING);
        }

        public BigDecimal getDiscount() {
            return discount.setScale(2, ROUNDING);
        }

        public BigDecimal getLineTotal() {
            BigDecimal total = getSubtotal().subtract(getDiscount());
            if (total.compareTo(BigDecimal.ZERO) < 0) total = BigDecimal.ZERO;
            return total.setScale(2, ROUNDING);
        }

        public void addDiscount(BigDecimal amount) {
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) return;
            BigDecimal max = getSubtotal().subtract(discount);
            if (amount.compareTo(max) > 0) amount = max;
            discount = discount.add(amount).setScale(2, ROUNDING);
        }
    }

    public record AppliedPromotion(UUID promotionId, String type, String description, BigDecimal amount, int priority) { }
}
