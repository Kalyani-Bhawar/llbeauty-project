package com.llbeauty.config;

import com.llbeauty.entity.Product;
import com.llbeauty.repository.ProductRepository;
import com.llbeauty.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AuthService authService;
    private final ProductRepository productRepository;

    @Override
    public void run(String... args) throws Exception {
        // 1. Create default admin
        authService.createDefaultAdminIfNotExists();

        // 2. Populate default products if DB is empty
        if (productRepository.count() == 0) {
            List<Product> defaultProducts = List.of(
                Product.builder()
                        .name("Rose Gold Elixir")
                        .category("Skincare")
                        .description("Advanced anti-aging serum with pure rose extract and gold particles.")
                        .price(2499.0)
                        .imageUrl("/images/skincare.png")
                        .stock(100)
                        .build(),
                Product.builder()
                        .name("Oud Majestic")
                        .category("Perfumes")
                        .description("Exclusive oriental fragrance with deep oud, amber and musk notes.")
                        .price(4999.0)
                        .imageUrl("/images/perfume.png")
                        .stock(50)
                        .build(),
                Product.builder()
                        .name("Keratin Pro Shampoo")
                        .category("Haircare")
                        .description("Salon-grade keratin formula for silky, frizz-free hair every day.")
                        .price(1299.0)
                        .imageUrl("/images/haircare.png")
                        .stock(150)
                        .build(),
                Product.builder()
                        .name("Luxury Detox Kit")
                        .category("Spa / Detox")
                        .description("Complete spa kit with essential oils, detox mask, and aromatherapy candles.")
                        .price(3499.0)
                        .imageUrl("/images/spa.png")
                        .stock(30)
                        .build()
            );
            productRepository.saveAll(defaultProducts);
            log.info("✅ Database seeded with default beauty products.");
        }
    }
}
