package com.llbeauty.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "merchant_order_items")
public class MerchantOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_order_id", nullable = false)
    private MerchantOrder merchantOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "mrp", nullable = false)
    private Double mrp;

    @Column(name = "merchant_discount_percent", nullable = false)
    private Double merchantDiscountPercent;

    @Column(name = "merchant_price", nullable = false)
    private Double merchantPrice;

    @Column(name = "bulk_discount_percent", nullable = false)
    private Double bulkDiscountPercent;

    @Column(name = "final_price", nullable = false)
    private Double finalPrice;

    public MerchantOrderItem() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MerchantOrder getMerchantOrder() {
        return merchantOrder;
    }

    public void setMerchantOrder(MerchantOrder merchantOrder) {
        this.merchantOrder = merchantOrder;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getMrp() {
        return mrp;
    }

    public void setMrp(Double mrp) {
        this.mrp = mrp;
    }

    public Double getMerchantDiscountPercent() {
        return merchantDiscountPercent;
    }

    public void setMerchantDiscountPercent(Double merchantDiscountPercent) {
        this.merchantDiscountPercent = merchantDiscountPercent;
    }

    public Double getMerchantPrice() {
        return merchantPrice;
    }

    public void setMerchantPrice(Double merchantPrice) {
        this.merchantPrice = merchantPrice;
    }

    public Double getBulkDiscountPercent() {
        return bulkDiscountPercent;
    }

    public void setBulkDiscountPercent(Double bulkDiscountPercent) {
        this.bulkDiscountPercent = bulkDiscountPercent;
    }

    public Double getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(Double finalPrice) {
        this.finalPrice = finalPrice;
    }
}
