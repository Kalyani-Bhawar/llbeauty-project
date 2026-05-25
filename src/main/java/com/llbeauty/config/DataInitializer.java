package com.llbeauty.config;

import com.llbeauty.entity.Membership;
import com.llbeauty.entity.Merchant;
import com.llbeauty.entity.Product;
import com.llbeauty.entity.QrCode;
import com.llbeauty.entity.SalonInfo;
import com.llbeauty.repository.MembershipRepository;
import com.llbeauty.repository.MerchantRepository;
import com.llbeauty.repository.ProductRepository;
import com.llbeauty.repository.QrCodeRepository;
import com.llbeauty.repository.SalonInfoRepository;
import com.llbeauty.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final AuthService authService;
    private final ProductRepository productRepository;
    private final SalonInfoRepository salonInfoRepository;
    private final MembershipRepository membershipRepository;
    private final MerchantRepository merchantRepository;
    private final QrCodeRepository qrCodeRepository;

    public DataInitializer(AuthService authService,
                           ProductRepository productRepository,
                           SalonInfoRepository salonInfoRepository,
                           MembershipRepository membershipRepository,
                           MerchantRepository merchantRepository,
                           QrCodeRepository qrCodeRepository) {
        this.authService = authService;
        this.productRepository = productRepository;
        this.salonInfoRepository = salonInfoRepository;
        this.membershipRepository = membershipRepository;
        this.merchantRepository = merchantRepository;
        this.qrCodeRepository = qrCodeRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. Reset and create default admin (deletes old records, inserts fresh)
        authService.resetAndCreateDefaultAdmin();

        // 2. Seed default Salon flagship info if empty
        if (salonInfoRepository.count() == 0) {
            SalonInfo flagship = new SalonInfo();
            flagship.setName("L.L. Beauty Salon");
            flagship.setTagline("Main Flagship Branch");
            flagship.setDescription("Our premium studio in Pune offers cutting-edge hair styling, skin treatments, and customized wellness therapies in a deeply relaxing luxury environment.");
            flagship.setAddress("123, Beauty Lane, Near City Mall, Pune, Maharashtra - 411001");
            flagship.setContactPhone("+91 98765 43210");
            flagship.setContactEmail("salon@llbeauty.com");
            flagship.setTimings("Mon - Sat: 10:00 AM - 07:00 PM (Sunday Closed)");
            flagship.setImageUrl("/images/salon_flagship.png");
            salonInfoRepository.save(flagship);
            log.info("Database seeded with default flagship salon branch details.");
        }

        // 3. Populate default products if DB is empty
        if (productRepository.count() == 0) {
            Product p1 = new Product();
            p1.setName("Rose Gold Elixir");
            p1.setCategory("Skincare");
            p1.setDescription("Advanced anti-aging serum with pure rose extract and gold particles.");
            p1.setPrice(2499.0);
            p1.setImageUrl("/images/skincare.png");
            p1.setStock(100);

            Product p2 = new Product();
            p2.setName("Oud Majestic");
            p2.setCategory("Perfumes");
            p2.setDescription("Exclusive oriental fragrance with deep oud, amber and musk notes.");
            p2.setPrice(4999.0);
            p2.setImageUrl("/images/perfume.png");
            p2.setStock(50);

            Product p3 = new Product();
            p3.setName("Keratin Pro Shampoo");
            p3.setCategory("Haircare");
            p3.setDescription("Salon-grade keratin formula for silky, frizz-free hair every day.");
            p3.setPrice(1299.0);
            p3.setImageUrl("/images/haircare.png");
            p3.setStock(150);

            Product p4 = new Product();
            p4.setName("Luxury Detox Kit");
            p4.setCategory("Spa / Detox");
            p4.setDescription("Complete spa kit with essential oils, detox mask, and aromatherapy candles.");
            p4.setPrice(3499.0);
            p4.setImageUrl("/images/spa.png");
            p4.setStock(30);

            productRepository.saveAll(List.of(p1, p2, p3, p4));
            log.info("Database seeded with default beauty products.");
        }

        // 4. Seed Membership Plans if empty
        if (membershipRepository.count() == 0) {
            Membership silver = new Membership(
                null,
                "Silver Beauty Pass",
                999.0,
                0.05,
                "5% Cashback on All Orders\nExclusive Birthday Coupon\nEarly Access to Sales & Launches\n100 Welcome Credits",
                365,
                100.0
            );

            Membership gold = new Membership(
                null,
                "Gold Glam Pass",
                2999.0,
                0.10,
                "10% Cashback on All Orders\nFree Courier Shipping\nDouble Reward Points on Salon\nVIP Customer Support Line\n10% Salon Discounts\n500 Welcome Credits",
                365,
                500.0
            );

            Membership black = new Membership(
                null,
                "Black Luxe Pass",
                7999.0,
                0.20,
                "20% Cashback on All Orders\nFree VIP Salon Access\nPriority Home Delivery\nPremium Gift Hamper at Signup\nExclusive Luxury Partner Offers\nPersonal Beauty Consultant\n1500 Welcome Credits",
                365,
                1500.0
            );

            membershipRepository.saveAll(List.of(silver, gold, black));
            log.info("Database seeded with default membership plans.");
        }

        // 5. Seed Merchants and QR Codes for local wallet redemption scanning
        if (merchantRepository.count() == 0) {
            Merchant flagshipSpa = new Merchant(
                null,
                "L.L. Beauty Flagship Spa",
                "+91 99999 88888",
                "Aundh, Pune",
                "ACTIVE",
                null
            );
            Merchant salonLounge = new Merchant(
                null,
                "L.L. Beauty Lounge",
                "+91 98888 77777",
                "Koregaon Park, Pune",
                "ACTIVE",
                null
            );

            merchantRepository.saveAll(List.of(flagshipSpa, salonLounge));

            // Generate active QR codes
            QrCode qr1 = new QrCode(null, flagshipSpa, "/wallet/redeem?merchantId=" + flagshipSpa.getId(), "ACTIVE", null);
            QrCode qr2 = new QrCode(null, salonLounge, "/wallet/redeem?merchantId=" + salonLounge.getId(), "ACTIVE", null);

            qrCodeRepository.saveAll(List.of(qr1, qr2));
            log.info("Database seeded with sample merchants and scan-to-pay QR codes.");
        }
    }
}
