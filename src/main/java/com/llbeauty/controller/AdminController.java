package com.llbeauty.controller;

import com.llbeauty.entity.*;
import com.llbeauty.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AppointmentRepository appointmentRepository;
    private final FranchiseLeadRepository franchiseLeadRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final SalonInfoRepository salonInfoRepository;
    private final MembershipRepository membershipRepository;
    private final UserMembershipRepository userMembershipRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final OrderRepository orderRepository;
    private final com.llbeauty.repository.PaymentRepository paymentRepository;
    private final com.llbeauty.repository.SalonServiceRepository salonServiceRepository;
    private final com.llbeauty.repository.ContactMessageRepository contactMessageRepository;

    public AdminController(AppointmentRepository appointmentRepository,
                           FranchiseLeadRepository franchiseLeadRepository,
                           UserRepository userRepository,
                           ProductRepository productRepository,
                           SalonInfoRepository salonInfoRepository,
                           MembershipRepository membershipRepository,
                           UserMembershipRepository userMembershipRepository,
                           WalletTransactionRepository walletTransactionRepository,
                           OrderRepository orderRepository,
                           com.llbeauty.repository.PaymentRepository paymentRepository,
                           com.llbeauty.repository.SalonServiceRepository salonServiceRepository,
                           com.llbeauty.repository.ContactMessageRepository contactMessageRepository) {
        this.appointmentRepository = appointmentRepository;
        this.franchiseLeadRepository = franchiseLeadRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.salonInfoRepository = salonInfoRepository;
        this.membershipRepository = membershipRepository;
        this.userMembershipRepository = userMembershipRepository;
        this.walletTransactionRepository = walletTransactionRepository;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.salonServiceRepository = salonServiceRepository;
        this.contactMessageRepository = contactMessageRepository;
    }

    // ==========================================
    //  DASHBOARD
    // ==========================================
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("activeTab", "dashboard");
        model.addAttribute("totalUsers", userRepository.count());
        model.addAttribute("totalOrders", orderRepository.count());
        model.addAttribute("totalMemberships", userMembershipRepository.count());
        model.addAttribute("totalAppointments", appointmentRepository.count());
        model.addAttribute("totalProducts", productRepository.count());
        model.addAttribute("totalLeads", franchiseLeadRepository.count());

        double totalRevenue = paymentRepository.findAll().stream()
                .filter(p -> p.getStatus() != null && "SUCCESS".equalsIgnoreCase(p.getStatus()))
                .mapToDouble(Payment::getAmount)
                .sum();
        model.addAttribute("totalRevenue", totalRevenue);

        List<WalletTransaction> recentTxns = walletTransactionRepository.findAll().stream()
                .filter(t -> t.getCreatedAt() != null)
                .sorted((t1, t2) -> t2.getCreatedAt().compareTo(t1.getCreatedAt()))
                .limit(5)
                .toList();
        model.addAttribute("recentTransactions", recentTxns);

        model.addAttribute("recentAppointments", appointmentRepository.findAll().stream()
                .filter(a -> a.getCreatedAt() != null)
                .sorted((a1, a2) -> a2.getCreatedAt().compareTo(a1.getCreatedAt()))
                .limit(5).toList());

        model.addAttribute("recentUsers", userRepository.findAll().stream()
                .filter(u -> u.getCreatedAt() != null)
                .sorted((u1, u2) -> u2.getCreatedAt().compareTo(u1.getCreatedAt()))
                .limit(5).toList());

        model.addAttribute("recentLeads", franchiseLeadRepository.findAll().stream()
                .limit(5).toList());

        return "admin/dashboard";
    }

    // ==========================================
    //  USERS CRUD
    // ==========================================
    @GetMapping("/users")
    public String viewUsers(Model model) {
        model.addAttribute("activeTab", "users");
        model.addAttribute("users", userRepository.findAll());
        return "admin/users";
    }

    @PostMapping("/users/{id}/update-wallet")
    public String updateUserWallet(@PathVariable("id") Long id, 
                                   @RequestParam("walletBalance") Double balance, 
                                   RedirectAttributes redirectAttributes) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setWalletBalance(java.math.BigDecimal.valueOf(balance));
            userRepository.save(user);
            redirectAttributes.addFlashAttribute("successMessage", "Wallet balance updated successfully for " + user.getName());
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found.");
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found.");
        }
        return "redirect:/admin/users";
    }

    // ==========================================
    //  APPOINTMENTS CRUD
    // ==========================================
    @GetMapping("/appointments")
    public String viewAppointments(Model model) {
        model.addAttribute("activeTab", "appointments");
        model.addAttribute("appointments", appointmentRepository.findAll());
        return "admin/appointments";
    }

    @PostMapping("/appointments/{id}/status")
    public String updateAppointmentStatus(@PathVariable("id") Long id, 
                                          @RequestParam("status") String status, 
                                          RedirectAttributes redirectAttributes) {
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(id);
        if (appointmentOpt.isPresent()) {
            Appointment app = appointmentOpt.get();
            app.setStatus(status);
            appointmentRepository.save(app);
            redirectAttributes.addFlashAttribute("successMessage", "Booking status updated successfully to " + status);
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Booking not found.");
        }
        return "redirect:/admin/appointments";
    }

    @PostMapping("/appointments/{id}/delete")
    public String deleteAppointment(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        if (appointmentRepository.existsById(id)) {
            appointmentRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Booking deleted successfully.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Booking not found.");
        }
        return "redirect:/admin/appointments";
    }

    // ==========================================
    //  FRANCHISE LEADS CRUD
    // ==========================================
    @GetMapping("/franchise-leads")
    public String viewFranchiseLeads(Model model) {
        model.addAttribute("activeTab", "franchise-leads");
        model.addAttribute("leads", franchiseLeadRepository.findAll());
        return "admin/franchise_leads";
    }

    @PostMapping("/franchise-leads/{id}/delete")
    public String deleteFranchiseLead(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        if (franchiseLeadRepository.existsById(id)) {
            franchiseLeadRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Franchise lead inquiry deleted successfully.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Franchise lead not found.");
        }
        return "redirect:/admin/franchise-leads";
    }

    @GetMapping("/franchise-leads/new")
    public String newFranchiseLeadForm(Model model) {
        model.addAttribute("activeTab", "franchise-leads");
        model.addAttribute("lead", new FranchiseLead());
        return "admin/franchise_form";
    }

    @PostMapping("/franchise-leads/save")
    public String saveFranchiseLead(@ModelAttribute("lead") FranchiseLead lead, RedirectAttributes redirectAttributes) {
        franchiseLeadRepository.save(lead);
        redirectAttributes.addFlashAttribute("successMessage", "Franchise lead added successfully!");
        return "redirect:/admin/franchise-leads";
    }

    // ==========================================
    //  PRODUCTS CRUD
    // ==========================================
    @GetMapping("/products")
    public String viewProducts(Model model) {
        model.addAttribute("activeTab", "products");
        model.addAttribute("products", productRepository.findAll());
        return "admin/products";
    }

    @GetMapping("/products/new")
    public String newProductForm(Model model) {
        model.addAttribute("activeTab", "products");
        model.addAttribute("product", new Product());
        return "admin/product_form";
    }

    @PostMapping("/products/save")
    public String saveProduct(@ModelAttribute("product") Product product, 
                              @RequestParam("imageFile") MultipartFile imageFile, 
                              RedirectAttributes redirectAttributes) {
        try {
            if (!imageFile.isEmpty()) {
                String imageUrl = saveUploadedFile(imageFile, "products");
                product.setImageUrl(imageUrl);
            } else {
                product.setImageUrl("/images/skincare.png"); // fallback default image
            }
            productRepository.save(product);
            redirectAttributes.addFlashAttribute("successMessage", "Product added successfully!");
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to upload image: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }

    @GetMapping("/products/edit/{id}")
    public String editProductForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Product> prodOpt = productRepository.findById(id);
        if (prodOpt.isPresent()) {
            model.addAttribute("activeTab", "products");
            model.addAttribute("product", prodOpt.get());
            return "admin/product_form";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Product not found.");
            return "redirect:/admin/products";
        }
    }

    @PostMapping("/products/update/{id}")
    public String updateProduct(@PathVariable("id") Long id, 
                                @ModelAttribute("product") Product productDetails, 
                                @RequestParam("imageFile") MultipartFile imageFile, 
                                RedirectAttributes redirectAttributes) {
        Optional<Product> prodOpt = productRepository.findById(id);
        if (prodOpt.isPresent()) {
            Product product = prodOpt.get();
            product.setName(productDetails.getName());
            product.setCategory(productDetails.getCategory());
            product.setDescription(productDetails.getDescription());
            product.setPrice(productDetails.getPrice());
            product.setStock(productDetails.getStock());
            
            try {
                if (!imageFile.isEmpty()) {
                    String imageUrl = saveUploadedFile(imageFile, "products");
                    product.setImageUrl(imageUrl);
                }
                productRepository.save(product);
                redirectAttributes.addFlashAttribute("successMessage", "Product updated successfully!");
            } catch (IOException e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to upload image: " + e.getMessage());
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Product not found.");
        }
        return "redirect:/admin/products";
    }

    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Product deleted successfully.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Product not found.");
        }
        return "redirect:/admin/products";
    }

    // ==========================================
    //  SALON INFO CRUD (Dynamic Flagship Details)
    // ==========================================
    @GetMapping("/salon-info")
    public String editSalonInfo(Model model) {
        model.addAttribute("activeTab", "salon-info");
        // We look for ID 1 (default Flagship branch seeded by DataInitializer)
        Optional<SalonInfo> infoOpt = salonInfoRepository.findById(1L);
        if (infoOpt.isPresent()) {
            model.addAttribute("salonInfo", infoOpt.get());
        } else {
            model.addAttribute("salonInfo", new SalonInfo());
        }
        return "admin/salon_info";
    }

    @PostMapping("/salon-info/save")
    public String saveSalonInfo(@ModelAttribute("salonInfo") SalonInfo salonInfo, 
                                @RequestParam("imageFile") MultipartFile imageFile, 
                                RedirectAttributes redirectAttributes) {
        Optional<SalonInfo> infoOpt = salonInfoRepository.findById(1L);
        SalonInfo flagship = infoOpt.orElse(salonInfo);
        
        // Update all values
        flagship.setId(1L); // always make sure it is 1L
        flagship.setName(salonInfo.getName());
        flagship.setTagline(salonInfo.getTagline());
        flagship.setDescription(salonInfo.getDescription());
        flagship.setAddress(salonInfo.getAddress());
        flagship.setContactPhone(salonInfo.getContactPhone());
        flagship.setContactEmail(salonInfo.getContactEmail());
        flagship.setTimings(salonInfo.getTimings());

        try {
            if (!imageFile.isEmpty()) {
                String imageUrl = saveUploadedFile(imageFile, "salon");
                flagship.setImageUrl(imageUrl);
            }
            salonInfoRepository.save(flagship);
            redirectAttributes.addFlashAttribute("successMessage", "Salon Flagship Information updated dynamically!");
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to upload image: " + e.getMessage());
        }
        return "redirect:/admin/salon-info";
    }

    // ==========================================
    //  DUAL PATH UPLOAD CORE HELPER
    // ==========================================
    private String saveUploadedFile(MultipartFile file, String subDir) throws IOException {
        if (file.isEmpty()) return null;
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename().replaceAll("\\s+", "_");
        
        // Absolute project root folder path
        String projectRoot = "d:/SpringBoot/llbeauty/llbeauty";
        
        // 1. Persist in static uploads source directory
        Path srcUploadPath = Paths.get(projectRoot, "src/main/resources/static/uploads", subDir);
        if (!Files.exists(srcUploadPath)) {
            Files.createDirectories(srcUploadPath);
        }
        Path srcFilePath = srcUploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), srcFilePath, StandardCopyOption.REPLACE_EXISTING);
        
        // 2. Persist in classes target folder (so the web app serves it dynamically instantly)
        Path targetUploadPath = Paths.get(projectRoot, "target/classes/static/uploads", subDir);
        if (!Files.exists(targetUploadPath)) {
            Files.createDirectories(targetUploadPath);
        }
        Path targetFilePath = targetUploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), targetFilePath, StandardCopyOption.REPLACE_EXISTING);
        
        return "/uploads/" + subDir + "/" + fileName;
    }

    // ==========================================
    //  MEMBERSHIPS CRUD & ANALYTICS
    // ==========================================
    @GetMapping("/memberships")
    public String viewMemberships(Model model) {
        model.addAttribute("activeTab", "memberships");
        model.addAttribute("memberships", membershipRepository.findAll());
        return "admin/memberships";
    }

    @PostMapping("/memberships/save")
    public String saveMembershipPlan(@ModelAttribute("membership") Membership plan, RedirectAttributes redirectAttributes) {
        membershipRepository.save(plan);
        redirectAttributes.addFlashAttribute("successMessage", "Membership plan updated successfully!");
        return "redirect:/admin/memberships";
    }

    // ==========================================
    //  WALLET TRANSACTIONS
    // ==========================================
    @GetMapping("/wallet-transactions")
    public String viewWalletTransactions(Model model) {
        model.addAttribute("activeTab", "wallet-transactions");
        model.addAttribute("transactions", walletTransactionRepository.findAll());
        return "admin/wallet_transactions";
    }

    // ==========================================
    //  ORDERS LIST
    // ==========================================
    @GetMapping("/orders")
    public String viewOrders(Model model) {
        model.addAttribute("activeTab", "orders");
        model.addAttribute("orders", orderRepository.findAll());
        return "admin/orders";
    }

    @GetMapping("/membership-users")
    public String viewMembershipUsers(Model model) {
        model.addAttribute("activeTab", "membership-users");
        List<UserMembership> userPlans = userMembershipRepository.findAll();
        model.addAttribute("userMemberships", userPlans);

        // Compute Membership Analytics
        double pinkRevenue = 0.0;
        double goldRevenue = 0.0;
        double blackRevenue = 0.0;
        long activeCount = 0;
        long expiredCount = 0;

        for (UserMembership um : userPlans) {
            String planName = um.getMembership().getName();
            double price = um.getMembership().getPrice();

            if ("ACTIVE".equals(um.getStatus())) {
                activeCount++;
            } else if ("EXPIRED".equals(um.getStatus())) {
                expiredCount++;
            }

            if (planName.contains("Pink")) {
                pinkRevenue += price;
            } else if (planName.contains("Gold")) {
                goldRevenue += price;
            } else if (planName.contains("Black")) {
                blackRevenue += price;
            }
        }

        model.addAttribute("totalRevenue", pinkRevenue + goldRevenue + blackRevenue);
        model.addAttribute("pinkRevenue", pinkRevenue);
        model.addAttribute("goldRevenue", goldRevenue);
        model.addAttribute("blackRevenue", blackRevenue);
        model.addAttribute("activeCount", activeCount);
        model.addAttribute("expiredCount", expiredCount);

        return "admin/membership_users";
    }

    // ==========================================
    //  SALON SERVICES CRUD
    // ==========================================
    @GetMapping("/salon-services")
    public String viewSalonServices(Model model) {
        model.addAttribute("activeTab", "salon-services");
        model.addAttribute("services", salonServiceRepository.findAll());
        return "admin/salon_services";
    }

    @GetMapping("/salon-services/new")
    public String newSalonServiceForm(Model model) {
        model.addAttribute("activeTab", "salon-services");
        model.addAttribute("service", new com.llbeauty.entity.SalonService());
        return "admin/service_form";
    }

    @PostMapping("/salon-services/save")
    public String saveSalonService(@ModelAttribute("service") com.llbeauty.entity.SalonService service,
                                   @RequestParam("imageFile") MultipartFile imageFile,
                                   RedirectAttributes redirectAttributes) {
        try {
            if (!imageFile.isEmpty()) {
                String imageUrl = saveUploadedFile(imageFile, "services");
                service.setImageUrl(imageUrl);
            } else if (service.getImageUrl() == null || service.getImageUrl().isEmpty()) {
                service.setImageUrl("/images/haircare.png");
            }
            salonServiceRepository.save(service);
            redirectAttributes.addFlashAttribute("successMessage", "Salon service saved successfully!");
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to upload image: " + e.getMessage());
        }
        return "redirect:/admin/salon-services";
    }

    @GetMapping("/salon-services/edit/{id}")
    public String editSalonServiceForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<com.llbeauty.entity.SalonService> sOpt = salonServiceRepository.findById(id);
        if (sOpt.isPresent()) {
            model.addAttribute("activeTab", "salon-services");
            model.addAttribute("service", sOpt.get());
            return "admin/service_form";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Salon service not found.");
            return "redirect:/admin/salon-services";
        }
    }

    @PostMapping("/salon-services/delete/{id}")
    public String deleteSalonService(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        if (salonServiceRepository.existsById(id)) {
            salonServiceRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Salon service deleted successfully.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Salon service not found.");
        }
        return "redirect:/admin/salon-services";
    }

    // ==========================================
    //  CONTACT MESSAGES VIEW
    // ==========================================
    @GetMapping("/contact-messages")
    public String viewContactMessages(Model model) {
        model.addAttribute("activeTab", "contact-messages");
        model.addAttribute("messages", contactMessageRepository.findAll());
        return "admin/messages";
    }

    @PostMapping("/contact-messages/delete/{id}")
    public String deleteContactMessage(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        if (contactMessageRepository.existsById(id)) {
            contactMessageRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Contact message deleted successfully.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Contact message not found.");
        }
        return "redirect:/admin/contact-messages";
    }
}
