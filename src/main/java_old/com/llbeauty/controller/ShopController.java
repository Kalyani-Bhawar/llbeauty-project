package com.llbeauty.controller;

import com.llbeauty.entity.Product;
import com.llbeauty.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ShopController {

    private final ProductRepository productRepository;

    @GetMapping("/shop")
    public String shop(Model model, @RequestParam(value = "category", required = false) String category) {
        List<Product> products;
        if (category != null && !category.trim().isEmpty()) {
            products = productRepository.findByCategory(category.trim());
        } else {
            products = productRepository.findAll();
        }
        model.addAttribute("products", products);
        model.addAttribute("selectedCategory", category);
        return "shop";
    }
}
