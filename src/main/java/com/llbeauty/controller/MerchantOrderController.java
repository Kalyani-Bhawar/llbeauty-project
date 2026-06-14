package com.llbeauty.controller;

import com.llbeauty.entity.Order;
import com.llbeauty.entity.OrderItem;
import com.llbeauty.entity.Product;
import com.llbeauty.entity.User;
import com.llbeauty.repository.OrderRepository;
import com.llbeauty.repository.ProductRepository;
import com.llbeauty.service.WalletService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/merchant/orders")
public class MerchantOrderController {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final WalletService walletService;

    public MerchantOrderController(ProductRepository productRepository, OrderRepository orderRepository, WalletService walletService) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.walletService = walletService;
    }

    @PostMapping("/bulk-checkout")
    public String checkoutBulkOrder(@AuthenticationPrincipal User user,
                                    @RequestParam Map<String, String> allParams,
                                    RedirectAttributes redirectAttributes) {
        if (user == null || !"ROLE_MERCHANT".equals(user.getRole())) {
            return "redirect:/login";
        }

        double totalAmount = 0.0;
        List<OrderItem> orderItems = new ArrayList<>();
        Order order = new Order();
        order.setUser(user);
        order.setOrderType("WHOLESALE");
        order.setStatus("SUCCESS"); // Assuming wallet payment is immediate
        order.setOrderStatus("Confirmed");

        // Parse products and quantities from form submission. 
        // Assuming form inputs are named like product_1=10, product_5=20
        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            if (entry.getKey().startsWith("product_")) {
                try {
                    Long productId = Long.parseLong(entry.getKey().replace("product_", ""));
                    int quantity = Integer.parseInt(entry.getValue());
                    if (quantity > 0) {
                        Optional<Product> pOpt = productRepository.findById(productId);
                        if (pOpt.isPresent()) {
                            Product product = pOpt.get();
                            double price = product.getWholesalePrice() != null ? product.getWholesalePrice() : product.getPrice();
                            double itemTotal = price * quantity;
                            totalAmount += itemTotal;

                            OrderItem item = new OrderItem();
                            item.setOrder(order);
                            item.setProduct(product);
                            item.setQuantity(quantity);
                            item.setPriceAtPurchase(price);
                            orderItems.add(item);
                        }
                    }
                } catch (NumberFormatException ignored) {}
            }
        }

        if (orderItems.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "No products selected for bulk order.");
            return "redirect:/merchant/wholesale";
        }

        // Deduct from wallet
        BigDecimal totalBd = BigDecimal.valueOf(totalAmount);
        boolean success = walletService.debit(user, totalBd, "Bulk Wholesale Purchase", "ORDER");
        
        if (!success) {
            redirectAttributes.addFlashAttribute("errorMessage", "Insufficient wallet balance to place this order.");
            return "redirect:/merchant/wholesale";
        }

        order.setTotalAmount(totalAmount);
        order.setItems(orderItems);
        orderRepository.save(order);

        redirectAttributes.addFlashAttribute("successMessage", "Wholesale order placed successfully!");
        return "redirect:/merchant/orders";
    }
}
