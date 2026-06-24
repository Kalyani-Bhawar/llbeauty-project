package com.llbeauty.service;

import com.llbeauty.dto.MerchantCartItemDTO;
import com.llbeauty.entity.Product;
import com.llbeauty.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * MerchantCartService — Single source of truth for all merchant cart calculations.
 *
 * Architecture rule: ALL price calculation logic lives here.
 * Controllers must NEVER recalculate prices independently.
 *
 * Pricing model:
 *   merchantPrice = mrp * (1 - merchantDiscountPercent / 100)
 *   bulkDiscountPercent = 5% for qty 11–50, 10% for qty 51+, else 0%
 *   finalPrice = mrp * (1 - merchantDiscountPercent/100) * (1 - bulkDiscountPercent/100)
 *   total = finalPrice * quantity
 */
@Service
public class MerchantCartService {

    private static final Logger log = LoggerFactory.getLogger(MerchantCartService.class);

    private final ProductRepository productRepository;

    public MerchantCartService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // ──────────────────────────────────────────────────────────────────
    // Public API
    // ──────────────────────────────────────────────────────────────────

    /**
     * Build a fully populated list of MerchantCartItemDTOs from a session cart.
     * Skips products that no longer exist in the database (safe null handling).
     *
     * @param cart Map of productId → quantity from HTTP session
     * @return list of DTOs ready for model / template use
     */
    public List<MerchantCartItemDTO> buildCartItems(Map<Long, Integer> cart) {
        List<MerchantCartItemDTO> items = new ArrayList<>();
        if (cart == null || cart.isEmpty()) return items;

        for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
            Product product = productRepository.findById(entry.getKey()).orElse(null);
            if (product == null) continue;

            int qty = entry.getValue();
            if (qty <= 0) continue;

            items.add(buildItem(product, qty));
   
            System.out.println("PRODUCT = " + product.getName());
        }
        return items;
    }

    /**
     * Calculate the grand total (sum of all line totals) from a pre-built item list.
     * Use this after buildCartItems() rather than recalculating from scratch.
     *
     * @param items list from buildCartItems()
     * @return grand total (finalPrice * qty for each item)
     */
    public double calculateGrandTotal(List<MerchantCartItemDTO> items) {
        return items.stream().mapToDouble(MerchantCartItemDTO::getTotal).sum();
    }

    /**
     * Calculate the MRP subtotal (sum of all mrpTotals).
     * Used to display "Subtotal (MRP)" on checkout.
     */
    public double calculateSubtotal(List<MerchantCartItemDTO> items) {
        return items.stream().mapToDouble(MerchantCartItemDTO::getMrpTotal).sum();
    }

    /**
     * Calculate total savings (sum of all item savings).
     * Used to display "Merchant Discount" on checkout.
     */
    public double calculateTotalSavings(List<MerchantCartItemDTO> items) {
        return items.stream().mapToDouble(MerchantCartItemDTO::getSavings).sum();
    }

    /**
     * Calculate total product discounts (merchant discount portion only, not bulk).
     * productDiscount per item = (mrp - merchantPrice) * qty
     */
    public double calculateProductDiscounts(List<MerchantCartItemDTO> items) {
        return items.stream()
                .mapToDouble(item -> (item.getMrp() - item.getMerchantPrice()) * item.getQuantity())
                .sum();
    }

    /**
     * Calculate total bulk discounts (bulk discount portion only).
     * bulkDiscount per item = (merchantPrice - finalPrice) * qty
     */
    public double calculateBulkDiscounts(List<MerchantCartItemDTO> items) {
        return items.stream()
                .mapToDouble(item -> (item.getMerchantPrice() - item.getFinalPrice()) * item.getQuantity())
                .sum();
    }

    // ──────────────────────────────────────────────────────────────────
    // Private helpers
    // ──────────────────────────────────────────────────────────────────

    /**
     * Build a single MerchantCartItemDTO for a given product and quantity.
     * This is the ONLY place the pricing formula is implemented.
     */
    private MerchantCartItemDTO buildItem(Product product, int qty) {
        // MRP = product.price (list/retail price). Log for debug visibility.
        double mrp = product.getPrice() != null ? product.getPrice()
                   : (product.getWholesalePrice() != null ? product.getWholesalePrice() : 0.0);
        log.debug("Building cart item: product='{}' id={} price={} mrp=resolved={}",
                product.getName(), product.getId(), product.getPrice(), mrp);

        // Merchant discount % — null-safe default to 0
        double merchantDiscPercent = product.getMerchantDiscount() != null
                ? product.getMerchantDiscount() : 0.0;
        double merchantDiscAmount = mrp * (merchantDiscPercent / 100.0);
        double merchantPrice = mrp - merchantDiscAmount;

        // Bulk discount % — tiered by quantity
        double bulkDiscPercent = resolveBulkDiscountPercent(qty);
        double bulkDiscAmount = merchantPrice * (bulkDiscPercent / 100.0);
        double finalPrice = merchantPrice - bulkDiscAmount;

        // Line totals
        double mrpTotal = mrp * qty;
        double lineTotal = finalPrice * qty;
        double savings = mrpTotal - lineTotal;

        MerchantCartItemDTO dto = new MerchantCartItemDTO();
        dto.setProduct(product);
        dto.setQuantity(qty);
        dto.setMrp(mrp);
        dto.setMerchantDiscountPercent(merchantDiscPercent);
        dto.setMerchantPrice(merchantPrice);
        dto.setBulkDiscountPercent(bulkDiscPercent);
        dto.setFinalPrice(finalPrice);
        dto.setTotal(lineTotal);
        dto.setMrpTotal(mrpTotal);
        dto.setSavings(savings);
        return dto;
    }

    /**
     * Resolve the bulk discount percentage based on quantity.
     *
     * Tiers:
     *   qty 1–10   → 0%
     *   qty 11–50  → 5%
     *   qty 51+    → 10%
     */
    private double resolveBulkDiscountPercent(int qty) {
        if (qty >= 51) return 10.0;
        if (qty >= 11) return 5.0;
        return 0.0;
    }
}
