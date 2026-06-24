package com.llbeauty.dto;

import com.llbeauty.entity.Product;

/**
 * DTO representing a single line item in the merchant (wholesale) cart.
 *
 * Field naming is deliberately aligned with both:
 *  - MerchantOrderItem entity (merchantDiscountPercent, merchantPrice, bulkDiscountPercent, finalPrice)
 *  - Thymeleaf templates (cart.html uses merchantPrice / total; checkout uses bulkPrice / merchantDiscountPercent)
 *
 * Single source of truth for all cart/checkout calculations.
 */
public class MerchantCartItemDTO {

    /** The full Product entity — used by templates that reference item.product.name / item.product.imageUrl */
    private Product product;

    /** Quantity ordered */
    private int quantity;

    /** MRP (original price) */
    private Double mrp;

    /** Merchant discount % (e.g. 15 means 15%) — null-safe, defaults to 0 */
    private double merchantDiscountPercent;

    /** Price after merchant discount only: mrp * (1 - merchantDiscountPercent/100) */
    private double merchantPrice;

    /**
     * Bulk discount % applied on top of merchant discount for large quantities.
     * Tiers: qty 11-50 → 5%, qty 51+ → 10%, else 0
     */
    private double bulkDiscountPercent;

    /**
     * Final unit price after both merchant discount AND bulk discount:
     * mrp * (1 - merchantDiscountPercent/100) * (1 - bulkDiscountPercent/100)
     * This is the "bulkPrice" referenced in merchant_checkout.html
     */
    private double finalPrice;

    /** Total for this line: finalPrice * quantity */
    private double total;

    /** MRP total for this line: mrp * quantity (used for subtotal display) */
    private double mrpTotal;

    /** Total savings for this line: (mrp - finalPrice) * quantity */
    private double savings;

    // ──────────────────────────────────────────────────
    // Getters & Setters
    // ──────────────────────────────────────────────────

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Double getMrp() {
        return mrp;
    }

    public void setMrp(Double mrp) {
        this.mrp = mrp;
    }

    public double getMerchantDiscountPercent() {
        return merchantDiscountPercent;
    }

    public void setMerchantDiscountPercent(double merchantDiscountPercent) {
        this.merchantDiscountPercent = merchantDiscountPercent;
    }

    public double getMerchantPrice() {
        return merchantPrice;
    }

    public void setMerchantPrice(double merchantPrice) {
        this.merchantPrice = merchantPrice;
    }

    public double getBulkDiscountPercent() {
        return bulkDiscountPercent;
    }

    public void setBulkDiscountPercent(double bulkDiscountPercent) {
        this.bulkDiscountPercent = bulkDiscountPercent;
    }

    /**
     * Alias for finalPrice — merchant_checkout.html references item.bulkPrice
     */
    public double getBulkPrice() {
        return finalPrice;
    }

    public double getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(double finalPrice) {
        this.finalPrice = finalPrice;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getMrpTotal() {
        return mrpTotal;
    }

    public void setMrpTotal(double mrpTotal) {
        this.mrpTotal = mrpTotal;
    }

    public double getSavings() {
        return savings;
    }

    public void setSavings(double savings) {
        this.savings = savings;
    }
}