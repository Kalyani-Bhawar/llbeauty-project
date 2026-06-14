package com.llbeauty.controller;

import com.llbeauty.entity.*;
import com.llbeauty.repository.*;
import com.llbeauty.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

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
    private final com.llbeauty.service.WalletService walletService;
    private final com.llbeauty.service.RewardService rewardService;
    private final com.llbeauty.repository.MembershipHistoryRepository membershipHistoryRepository;
    private final com.llbeauty.repository.MemberProfileRepository memberProfileRepository;
    private final com.llbeauty.repository.ManualPaymentRequestRepository manualPaymentRequestRepository;
    private final com.llbeauty.service.PaymentService paymentService;
    private final com.llbeauty.service.MembershipService membershipService;
    private final NotificationService notificationService;
    private final BCryptPasswordEncoder passwordEncoder;

    @org.springframework.beans.factory.annotation.Value("${app.upload.root}")
    private String projectRoot;

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
                           com.llbeauty.repository.ContactMessageRepository contactMessageRepository,
                           com.llbeauty.service.WalletService walletService,
                           com.llbeauty.service.RewardService rewardService,
                           com.llbeauty.repository.MembershipHistoryRepository membershipHistoryRepository,
                           com.llbeauty.repository.MemberProfileRepository memberProfileRepository,
                           com.llbeauty.repository.ManualPaymentRequestRepository manualPaymentRequestRepository,
                           com.llbeauty.service.PaymentService paymentService,
                           com.llbeauty.service.MembershipService membershipService,
                           NotificationService notificationService,
                           BCryptPasswordEncoder passwordEncoder) {
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
        this.walletService = walletService;
        this.rewardService = rewardService;
        this.membershipHistoryRepository = membershipHistoryRepository;
        this.memberProfileRepository = memberProfileRepository;
        this.manualPaymentRequestRepository = manualPaymentRequestRepository;
        this.paymentService = paymentService;
        this.membershipService = membershipService;
        this.notificationService = notificationService;
        this.passwordEncoder = passwordEncoder;
    }

    @org.springframework.beans.factory.annotation.Autowired
    private com.llbeauty.repository.AuditLogRepository auditLogRepository;

    @ModelAttribute
    public void addCommonAttributes(Model model) {
        model.addAttribute("notifications", notificationService.getRecentNotifications());
        model.addAttribute("unreadNotificationCount", notificationService.getUnreadCount());
    }

    @GetMapping({"", "/"})
    public String adminRoot() {
        return "redirect:/admin/dashboard";
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

        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        double todayRevenue = paymentRepository.findAll().stream()
                .filter(p -> p.getCreatedAt() != null && p.getCreatedAt().isAfter(startOfDay))
                .filter(p -> p.getStatus() != null && "SUCCESS".equalsIgnoreCase(p.getStatus()))
                .mapToDouble(Payment::getAmount)
                .sum();
        model.addAttribute("todayRevenue", todayRevenue);

        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        double monthlyRevenue = paymentRepository.findAll().stream()
                .filter(p -> p.getCreatedAt() != null && p.getCreatedAt().isAfter(startOfMonth))
                .filter(p -> p.getStatus() != null && "SUCCESS".equalsIgnoreCase(p.getStatus()))
                .mapToDouble(Payment::getAmount)
                .sum();
        model.addAttribute("monthlyRevenue", monthlyRevenue);

        double totalWalletBalance = userRepository.findAll().stream()
                .mapToDouble(u -> u.getWalletBalance() != null ? u.getWalletBalance().doubleValue() : 0.0)
                .sum();
        model.addAttribute("totalWalletBalance", totalWalletBalance);

        long pendingBookings = appointmentRepository.countByStatus("PENDING");
        model.addAttribute("pendingBookings", pendingBookings);

        long activeMembers = userMembershipRepository.countByStatus("ACTIVE");
        model.addAttribute("activeMembers", activeMembers);

        // Recent lists (top 5)
        model.addAttribute("recentOrders", orderRepository.findAll().stream()
                .filter(o -> o.getCreatedAt() != null)
                .sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()))
                .limit(5).toList());

        model.addAttribute("recentTransactions", walletTransactionRepository.findAll().stream()
                .filter(t -> t.getCreatedAt() != null)
                .sorted((t1, t2) -> t2.getCreatedAt().compareTo(t1.getCreatedAt()))
                .limit(5).toList());

        model.addAttribute("recentAppointments", appointmentRepository.findAll().stream()
                .filter(a -> a.getCreatedAt() != null)
                .sorted((a1, a2) -> a2.getCreatedAt().compareTo(a1.getCreatedAt()))
                .limit(5).toList());

        model.addAttribute("recentUsers", userRepository.findAll().stream()
                .filter(u -> u.getCreatedAt() != null)
                .sorted((u1, u2) -> u2.getCreatedAt().compareTo(u1.getCreatedAt()))
                .limit(5).toList());

        model.addAttribute("recentLeads", franchiseLeadRepository.findAll().stream()
                .filter(l -> l.getCreatedAt() != null)
                .sorted((l1, l2) -> l2.getCreatedAt().compareTo(l1.getCreatedAt()))
                .limit(5).toList());

        return "admin/dashboard";
    }

    // ==========================================
    //  USERS CRUD & ACTIONS
    // ==========================================
    @GetMapping("/users")
    public String viewUsers(@RequestParam(value = "search", required = false) String search,
                            @RequestParam(value = "blocked", required = false) Boolean blocked,
                            @RequestParam(value = "page", defaultValue = "0") int page,
                            @RequestParam(value = "size", defaultValue = "10") int size,
                            Model model) {
        model.addAttribute("activeTab", "users");
        Pageable pageable = PageRequest.of(page, size);
        Page<User> usersPage = userRepository.searchUsers(search, blocked, pageable);
        
        model.addAttribute("users", usersPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", usersPage.getTotalPages());
        model.addAttribute("totalElements", usersPage.getTotalElements());
        model.addAttribute("search", search);
        model.addAttribute("blocked", blocked);
        return "admin/users";
    }

    @PostMapping("/users/{id}/block")
    public String blockUser(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setIsBlocked(true);
            userRepository.save(user);
            redirectAttributes.addFlashAttribute("successMessage", "User " + user.getName() + " blocked successfully.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found.");
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/unblock")
    public String unblockUser(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setIsBlocked(false);
            userRepository.save(user);
            redirectAttributes.addFlashAttribute("successMessage", "User " + user.getName() + " unblocked successfully.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found.");
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/reset-password")
    public String resetUserPassword(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPassword(passwordEncoder.encode("123456"));
            userRepository.save(user);
            redirectAttributes.addFlashAttribute("successMessage", "Password reset to '123456' for " + user.getName());
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found.");
        }
        return "redirect:/admin/users";
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

    @GetMapping("/users/export")
    public void exportUsers(@RequestParam(value = "search", required = false) String search,
                            @RequestParam(value = "blocked", required = false) Boolean blocked,
                            HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=users.csv");
        PrintWriter writer = response.getWriter();
        writer.println("ID,Name,Email,Mobile,Wallet Balance,Blocked Status,Created At");

        List<User> users = userRepository.searchUsersList(search, blocked);
        for (User u : users) {
            writer.println(String.format("%d,%s,%s,%s,%.2f,%b,%s",
                u.getId(),
                u.getName().replace(",", " "),
                u.getEmail(),
                u.getMobile() != null ? u.getMobile() : "",
                u.getWalletBalance() != null ? u.getWalletBalance().doubleValue() : 0.0,
                u.getIsBlocked() != null ? u.getIsBlocked() : false,
                u.getCreatedAt()
            ));
        }
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable("id") Long id, 
                             org.springframework.security.core.Authentication authentication, 
                             RedirectAttributes redirectAttributes) {
        String currentEmail = authentication != null ? authentication.getName() : "admin";
        Optional<User> userToDelete = userRepository.findById(id);
        if (userToDelete.isPresent()) {
            if (currentEmail.equals(userToDelete.get().getEmail())) {
                redirectAttributes.addFlashAttribute("errorMessage", "You cannot delete your own account.");
                return "redirect:/admin/users";
            }
            User user = userToDelete.get();
            user.setActive(false);
            userRepository.save(user);
            
            AuditLog log = new AuditLog("USER_DELETED", "User " + user.getEmail() + " deleted", currentEmail);
            auditLogRepository.save(log);

            redirectAttributes.addFlashAttribute("successMessage", "User soft-deleted successfully.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found.");
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/activate")
    public String activateUser(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setActive(true);
            userRepository.save(user);
            redirectAttributes.addFlashAttribute("successMessage", "User activated successfully.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found.");
        }
        return "redirect:/admin/users";
    }

    // ==========================================
    //  APPOINTMENTS (SALON BOOKINGS)
    // ==========================================
    @GetMapping("/appointments")
    public String viewAppointments(@RequestParam(value = "search", required = false) String search,
                                   @RequestParam(value = "status", required = false) String status,
                                   @RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "size", defaultValue = "10") int size,
                                   Model model) {
        model.addAttribute("activeTab", "appointments");
        Pageable pageable = PageRequest.of(page, size);
        Page<Appointment> appPage = appointmentRepository.searchAppointments(search, status, pageable);

        model.addAttribute("appointments", appPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", appPage.getTotalPages());
        model.addAttribute("totalElements", appPage.getTotalElements());
        model.addAttribute("search", search);
        model.addAttribute("status", status);
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

    @GetMapping("/appointments/export")
    public void exportAppointments(@RequestParam(value = "search", required = false) String search,
                                   @RequestParam(value = "status", required = false) String status,
                                   HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=appointments.csv");
        PrintWriter writer = response.getWriter();
        writer.println("Booking ID,Customer Name,Mobile,Service,Date,Time Slot,Advance Paid,Payment Status,Status,Token");

        List<Appointment> apps = appointmentRepository.searchAppointmentsList(search, status);
        for (Appointment a : apps) {
            writer.println(String.format("%d,%s,%s,%s,%s,%s,%.2f,%s,%s,%s",
                a.getId(),
                a.getUserName() != null ? a.getUserName().replace(",", " ") : "",
                a.getUserMobile() != null ? a.getUserMobile() : "",
                a.getServiceName() != null ? a.getServiceName().replace(",", " ") : "",
                a.getAppointmentDate(),
                a.getTimeSlot(),
                a.getAdvancePaid() != null ? a.getAdvancePaid() : 0.0,
                a.getPaymentStatus() != null ? a.getPaymentStatus() : "",
                a.getStatus(),
                a.getToken() != null ? a.getToken() : ""
            ));
        }
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
    //  FRANCHISE LEADS CRUD & ACTIONS
    // ==========================================
    @GetMapping("/franchise-leads")
    public String viewFranchiseLeads(@RequestParam(value = "search", required = false) String search,
                                     @RequestParam(value = "status", required = false) String status,
                                     @RequestParam(value = "page", defaultValue = "0") int page,
                                     @RequestParam(value = "size", defaultValue = "10") int size,
                                     Model model) {
        model.addAttribute("activeTab", "franchise-leads");
        Pageable pageable = PageRequest.of(page, size);
        Page<FranchiseLead> leadsPage = franchiseLeadRepository.searchLeads(search, status, pageable);

        model.addAttribute("leads", leadsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", leadsPage.getTotalPages());
        model.addAttribute("totalElements", leadsPage.getTotalElements());
        model.addAttribute("search", search);
        model.addAttribute("status", status);
        return "admin/franchise_leads";
    }

    @PostMapping("/franchise-leads/{id}/status")
    public String updateFranchiseLeadStatus(@PathVariable("id") Long id,
                                            @RequestParam("status") String status,
                                            @RequestParam(value = "remarks", required = false) String remarks,
                                            RedirectAttributes redirectAttributes) {
        Optional<FranchiseLead> leadOpt = franchiseLeadRepository.findById(id);
        if (leadOpt.isPresent()) {
            FranchiseLead lead = leadOpt.get();
            lead.setStatus(status);
            if (remarks != null) {
                lead.setRemarks(remarks);
            }
            franchiseLeadRepository.save(lead);
            redirectAttributes.addFlashAttribute("successMessage", "Franchise lead status updated to " + status);
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Franchise lead not found.");
        }
        return "redirect:/admin/franchise-leads";
    }

    @GetMapping("/franchise-leads/export")
    public void exportFranchiseLeads(@RequestParam(value = "search", required = false) String search,
                                     @RequestParam(value = "status", required = false) String status,
                                     HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=franchise_leads.csv");
        PrintWriter writer = response.getWriter();
        writer.println("ID,Name,Email,Mobile,City,State,Budget,Franchise Type,Business Type,Experience,Message,Status,Remarks,Created At");

        List<FranchiseLead> leads = franchiseLeadRepository.searchLeadsList(search, status);
        for (FranchiseLead l : leads) {
            writer.println(String.format("%d,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
                l.getId(),
                l.getName().replace(",", " "),
                l.getEmail(),
                l.getMobile(),
                l.getCity() != null ? l.getCity() : "",
                l.getState() != null ? l.getState() : "",
                l.getBudget() != null ? l.getBudget() : "",
                l.getFranchiseType() != null ? l.getFranchiseType() : "",
                l.getBusinessType() != null ? l.getBusinessType() : "",
                l.getExperience() != null ? l.getExperience() : "",
                l.getMessage() != null ? l.getMessage().replace("\n", " ").replace(",", " ") : "",
                l.getStatus() != null ? l.getStatus() : "",
                l.getRemarks() != null ? l.getRemarks().replace("\n", " ").replace(",", " ") : "",
                l.getCreatedAt()
            ));
        }
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

    @PostMapping("/franchise-leads/{id}/update")
    public String updateFranchiseLead(@PathVariable("id") Long id, 
                                      @RequestParam("status") String status, 
                                      @RequestParam(value = "remarks", required = false) String remarks, 
                                      RedirectAttributes redirectAttributes) {
        Optional<FranchiseLead> leadOpt = franchiseLeadRepository.findById(id);
        if (leadOpt.isPresent()) {
            FranchiseLead lead = leadOpt.get();
            lead.setStatus(status);
            if (remarks != null)
                lead.setRemarks(remarks);
            franchiseLeadRepository.save(lead);
            redirectAttributes.addFlashAttribute("successMessage", "Franchise lead updated successfully.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Franchise lead not found.");
        }
        return "redirect:/admin/franchise-leads";
    }

    // ==========================================
    //  PRODUCTS CRUD & ACTIONS
    // ==========================================
    @GetMapping("/products")
    public String viewProducts(@RequestParam(value = "search", required = false) String search,
                               @RequestParam(value = "category", required = false) String category,
                               @RequestParam(value = "status", required = false) String status,
                               @RequestParam(value = "page", defaultValue = "0") int page,
                               @RequestParam(value = "size", defaultValue = "10") int size,
                               Model model) {
        model.addAttribute("activeTab", "products");
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> prodPage = productRepository.searchProductsPaged(category, search, status, pageable);

        model.addAttribute("products", prodPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", prodPage.getTotalPages());
        model.addAttribute("totalElements", prodPage.getTotalElements());
        model.addAttribute("search", search);
        model.addAttribute("category", category);
        model.addAttribute("status", status);
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
            product.setStatus(productDetails.getStatus());
            
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
    //  SALON INFO CRUD
    // ==========================================
    @GetMapping("/salon-info")
    public String editSalonInfo(Model model) {
        model.addAttribute("activeTab", "salon-info");
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
        
        flagship.setId(1L);
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

    private static final java.util.List<String> ALLOWED_IMAGE_TYPES = java.util.Arrays.asList("image/jpeg", "image/png", "image/webp", "image/gif");

    private String saveUploadedFile(MultipartFile file, String subDir) throws IOException {
        if (file.isEmpty()) return null;
        
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new IOException("Only image files (JPEG, PNG, WEBP, GIF) are allowed!");
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename().replaceAll("\\s+", "_");
        String root = (this.projectRoot != null && !this.projectRoot.isEmpty()) ? this.projectRoot : ".";
        byte[] fileBytes = file.getBytes();
        
        Path srcUploadPath = Paths.get(root, "src/main/resources/static/uploads", subDir);
        if (!Files.exists(srcUploadPath)) {
            Files.createDirectories(srcUploadPath);
        }
        Path srcFilePath = srcUploadPath.resolve(fileName);
        Files.write(srcFilePath, fileBytes);
        
        Path targetUploadPath = Paths.get(root, "target/classes/static/uploads", subDir);
        if (!Files.exists(targetUploadPath)) {
            Files.createDirectories(targetUploadPath);
        }
        Path targetFilePath = targetUploadPath.resolve(fileName);
        Files.write(targetFilePath, fileBytes);
        
        return "/uploads/" + subDir + "/" + fileName;
    }

    // ==========================================
    //  MEMBERSHIPS CRUD & VIP MEMBERS
    // ==========================================
    @GetMapping("/memberships")
    public String viewMemberships(Model model) {
        model.addAttribute("activeTab", "memberships");
        model.addAttribute("memberships", membershipRepository.findAll());
        return "admin/memberships";
    }

    @GetMapping("/memberships/new")
    public String newMembershipForm(Model model) {
        model.addAttribute("activeTab", "memberships");
        model.addAttribute("membership", new com.llbeauty.entity.Membership());
        return "admin/membership_form";
    }

    @GetMapping("/memberships/edit/{id}")
    public String editMembershipForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<com.llbeauty.entity.Membership> mOpt = membershipRepository.findById(id);
        if (mOpt.isPresent()) {
            model.addAttribute("activeTab", "memberships");
            model.addAttribute("membership", mOpt.get());
            return "admin/membership_form";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Membership plan not found.");
            return "redirect:/admin/memberships";
        }
    }

    @PostMapping("/memberships/delete/{id}")
    public String deleteMembership(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Optional<com.llbeauty.entity.Membership> mOpt = membershipRepository.findById(id);
        if (mOpt.isPresent()) {
            com.llbeauty.entity.Membership m = mOpt.get();
            m.setActive(false);
            membershipRepository.save(m);
            redirectAttributes.addFlashAttribute("successMessage", "Membership plan soft-deleted successfully.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Membership plan not found.");
        }
        return "redirect:/admin/memberships";
    }

    @PostMapping("/memberships/save")
    public String saveMembershipPlan(@ModelAttribute("membership") Membership plan, RedirectAttributes redirectAttributes) {
        membershipRepository.save(plan);
        redirectAttributes.addFlashAttribute("successMessage", "Membership plan updated successfully!");
        return "redirect:/admin/memberships";
    }

    @GetMapping("/membership-users")
    public String viewMembershipUsers(@RequestParam(value = "search", required = false) String search,
                                      @RequestParam(value = "status", required = false) String status,
                                      @RequestParam(value = "planName", required = false) String planName,
                                      @RequestParam(value = "page", defaultValue = "0") int page,
                                      @RequestParam(value = "size", defaultValue = "10") int size,
                                      @RequestParam(value = "viewUserId", required = false) Long viewUserId,
                                      Model model) {
        model.addAttribute("activeTab", "membership-users");
        
        Pageable pageable = PageRequest.of(page, size);
        Page<UserMembership> userPlansPage = userMembershipRepository.searchMemberships(search, status, planName, pageable);
        
        model.addAttribute("userMemberships", userPlansPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPlansPage.getTotalPages());
        model.addAttribute("totalElements", userPlansPage.getTotalElements());
        model.addAttribute("search", search);
        model.addAttribute("status", status);
        model.addAttribute("planName", planName);

        // Compute Membership Analytics
        double pinkRevenue = 0.0;
        double goldRevenue = 0.0;
        double blackRevenue = 0.0;
        long activeCount = 0;
        long expiredCount = 0;

        List<UserMembership> allRecords = userMembershipRepository.findAll();
        for (UserMembership um : allRecords) {
            String plan = um.getMembership().getName();
            double price = um.getMembership().getPrice();

            if ("ACTIVE".equals(um.getStatus())) {
                activeCount++;
            } else if ("EXPIRED".equals(um.getStatus())) {
                expiredCount++;
            }

            if (plan.contains("Pink")) {
                pinkRevenue += price;
            } else if (plan.contains("Gold")) {
                goldRevenue += price;
            } else if (plan.contains("Black")) {
                blackRevenue += price;
            }
        }

        model.addAttribute("totalRevenue", pinkRevenue + goldRevenue + blackRevenue);
        model.addAttribute("pinkRevenue", pinkRevenue);
        model.addAttribute("goldRevenue", goldRevenue);
        model.addAttribute("blackRevenue", blackRevenue);
        model.addAttribute("activeCount", activeCount);
        model.addAttribute("expiredCount", expiredCount);

        if (viewUserId != null) {
            Optional<User> uOpt = userRepository.findById(viewUserId);
            if (uOpt.isPresent()) {
                User u = uOpt.get();
                model.addAttribute("selectedUser", u);
                model.addAttribute("selectedUserWalletBalance", walletService.getBalance(u));
                model.addAttribute("selectedUserWalletTransactions", walletService.getTransactionHistory(u));
                model.addAttribute("selectedUserRewardPoint", rewardService.getPoints(u));
                model.addAttribute("selectedUserRewardTransactions", rewardService.getTransactionHistory(u));
                model.addAttribute("selectedUserHistory", membershipHistoryRepository.findByUserOrderByStartDateDesc(u));
            }
        }

        return "admin/membership_users";
    }

    @PostMapping("/membership/deactivate/{id}")
    public String deactivateMembership(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Optional<UserMembership> umOpt = userMembershipRepository.findById(id);
        if (umOpt.isPresent()) {
            UserMembership um = umOpt.get();
            um.setStatus("EXPIRED");
            userMembershipRepository.save(um);
            
            memberProfileRepository.findByUser(um.getUser()).ifPresent(p -> {
                p.setMembershipType("EXPIRED");
                memberProfileRepository.save(p);
            });

            List<MembershipHistory> histories = membershipHistoryRepository.findByUserOrderByStartDateDesc(um.getUser());
            for (MembershipHistory h : histories) {
                if (h.getPlanName().equalsIgnoreCase(um.getMembership().getName()) && "ACTIVE".equals(h.getStatus())) {
                    h.setStatus("EXPIRED");
                    membershipHistoryRepository.save(h);
                }
            }

            redirectAttributes.addFlashAttribute("successMessage", "Membership de-activated successfully.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Membership not found.");
        }
        return "redirect:/admin/membership-users";
    }

    @PostMapping("/membership/activate/{id}")
    public String activateMembership(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Optional<UserMembership> umOpt = userMembershipRepository.findById(id);
        if (umOpt.isPresent()) {
            UserMembership um = umOpt.get();
            um.setStatus("ACTIVE");
            um.setExpiryDate(LocalDateTime.now().plusDays(um.getMembership().getDurationDays()));
            userMembershipRepository.save(um);

            memberProfileRepository.findByUser(um.getUser()).ifPresent(p -> {
                p.setMembershipType(um.getMembership().getName());
                memberProfileRepository.save(p);
            });

            MembershipHistory history = new MembershipHistory();
            history.setUser(um.getUser());
            history.setPlanName(um.getMembership().getName());
            history.setPrice(BigDecimal.valueOf(um.getMembership().getPrice()));
            history.setStartDate(LocalDateTime.now());
            history.setExpiryDate(um.getExpiryDate());
            history.setStatus("ACTIVE");
            history.setPaymentId(um.getRazorpayPaymentId() != null ? um.getRazorpayPaymentId() : "ADMIN_MANUAL");
            membershipHistoryRepository.save(history);

            redirectAttributes.addFlashAttribute("successMessage", "Membership activated successfully.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Membership not found.");
        }
        return "redirect:/admin/membership-users";
    }

    @PostMapping("/membership/extend/{id}")
    public String extendMembership(@PathVariable("id") Long id, 
                                   @RequestParam("months") int months, 
                                   RedirectAttributes redirectAttributes) {
        Optional<UserMembership> umOpt = userMembershipRepository.findById(id);
        if (umOpt.isPresent()) {
            UserMembership um = umOpt.get();
            um.setExpiryDate(um.getExpiryDate().plusMonths(months));
            userMembershipRepository.save(um);

            redirectAttributes.addFlashAttribute("successMessage", "Membership extended successfully by " + months + " months.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Membership not found.");
        }
        return "redirect:/admin/membership-users";
    }

    @GetMapping("/membership-users/export")
    public void exportVIPMembers(@RequestParam(value = "search", required = false) String search,
                                 @RequestParam(value = "status", required = false) String status,
                                 @RequestParam(value = "planName", required = false) String planName,
                                 HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=vip_members.csv");
        PrintWriter writer = response.getWriter();
        writer.println("Member ID,User Name,Email,Mobile,Plan Name,Start Date,Expiry Date,DOB,Payment ID,Referral,Status");

        List<UserMembership> members = userMembershipRepository.searchMembershipsList(search, status, planName);
        for (UserMembership um : members) {
            writer.println(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
                um.getMemberId() != null ? um.getMemberId() : "",
                um.getUser() != null ? um.getUser().getName().replace(",", " ") : "",
                um.getUser() != null ? um.getUser().getEmail() : "",
                um.getUser() != null ? um.getUser().getMobile() : "",
                um.getMembership().getName(),
                um.getStartDate(),
                um.getExpiryDate(),
                um.getDob() != null ? um.getDob() : "",
                um.getRazorpayPaymentId() != null ? um.getRazorpayPaymentId() : "",
                um.getReferralCode() != null ? um.getReferralCode() : "",
                um.getStatus()
            ));
        }
    }

    // ==========================================
    //  WALLET TRANSACTIONS (Wallet Ledger)
    // ==========================================
    @GetMapping("/wallet-transactions")
    public String viewWalletTransactions(@RequestParam(value = "search", required = false) String search,
                                         @RequestParam(value = "type", required = false) String type,
                                         @RequestParam(value = "page", defaultValue = "0") int page,
                                         @RequestParam(value = "size", defaultValue = "10") int size,
                                         Model model) {
        model.addAttribute("activeTab", "wallet-transactions");
        Pageable pageable = PageRequest.of(page, size);
        Page<WalletTransaction> txPage = walletTransactionRepository.searchTransactions(search, type, pageable);

        model.addAttribute("transactions", txPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", txPage.getTotalPages());
        model.addAttribute("totalElements", txPage.getTotalElements());
        model.addAttribute("search", search);
        model.addAttribute("type", type);
        return "admin/wallet_transactions";
    }

    @GetMapping("/wallet-transactions/export")
    public void exportWalletTransactions(@RequestParam(value = "search", required = false) String search,
                                         @RequestParam(value = "type", required = false) String type,
                                         HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=wallet_ledger.csv");
        PrintWriter writer = response.getWriter();
        writer.println("Transaction ID,Customer Name,Email,Credit,Debit,Description,Reason,Date");

        List<WalletTransaction> txs = walletTransactionRepository.searchTransactionsList(search, type);
        for (WalletTransaction t : txs) {
            double amount = t.getAmount() != null ? t.getAmount().doubleValue() : 0.0;
            String credit = "CREDIT".equalsIgnoreCase(t.getType()) || "REFUND".equalsIgnoreCase(t.getType()) || "TOPUP".equalsIgnoreCase(t.getType()) ? String.format("%.2f", amount) : "0.00";
            String debit = "DEBIT".equalsIgnoreCase(t.getType()) || "PURCHASE".equalsIgnoreCase(t.getType()) ? String.format("%.2f", amount) : "0.00";

            writer.println(String.format("%d,%s,%s,%s,%s,%s,%s,%s",
                t.getId(),
                t.getUser() != null ? t.getUser().getName().replace(",", " ") : "",
                t.getUser() != null ? t.getUser().getEmail() : "",
                credit,
                debit,
                t.getDescription() != null ? t.getDescription().replace(",", " ") : "",
                t.getSource() != null ? t.getSource() : "",
                t.getCreatedAt()
            ));
        }
    }

    // ==========================================
    //  ORDERS MODULE
    // ==========================================
    @GetMapping("/orders")
    public String viewOrders(@RequestParam(value = "search", required = false) String search,
                             @RequestParam(value = "status", required = false) String status,
                             @RequestParam(value = "page", defaultValue = "0") int page,
                             @RequestParam(value = "size", defaultValue = "10") int size,
                             Model model) {
        model.addAttribute("activeTab", "orders");
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orderPage = orderRepository.searchOrders(search, status, pageable);

        model.addAttribute("orders", orderPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orderPage.getTotalPages());
        model.addAttribute("totalElements", orderPage.getTotalElements());
        model.addAttribute("search", search);
        model.addAttribute("status", status);
        return "admin/orders";
    }

    @PostMapping("/orders/{id}/cancel")
    public String cancelOrder(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Optional<Order> orderOpt = orderRepository.findById(id);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            if ("SUCCESS".equals(order.getStatus()) || "PENDING".equals(order.getStatus())) {
                order.setStatus("FAILED"); // cancel is marked as FAILED in status flow
                orderRepository.save(order);
                redirectAttributes.addFlashAttribute("successMessage", "Order #" + id + " cancelled successfully.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Order cannot be cancelled in status: " + order.getStatus());
            }
        }
        return "redirect:/admin/orders";
    }

    @PostMapping("/orders/{id}/update-status")
    public String updateOrderStatus(@PathVariable("id") Long id, 
                                    @RequestParam("orderStatus") String orderStatus, 
                                    org.springframework.security.core.Authentication authentication,
                                    RedirectAttributes redirectAttributes) {
        Optional<Order> orderOpt = orderRepository.findById(id);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.setOrderStatus(orderStatus);
            orderRepository.save(order);
            
            String currentEmail = authentication != null ? authentication.getName() : "admin";
            AuditLog log = new AuditLog("ORDER_UPDATED", "Order #" + id + " status updated to " + orderStatus, currentEmail);
            auditLogRepository.save(log);

            redirectAttributes.addFlashAttribute("successMessage", "Order #" + id + " status updated to " + orderStatus);
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Order not found.");
        }
        return "redirect:/admin/orders";
    }

    @PostMapping("/orders/{id}/refund")
    public String refundOrder(@PathVariable("id") Long id, 
                              @RequestParam("refundMethod") String refundMethod, 
                              RedirectAttributes redirectAttributes) {
        Optional<Order> orderOpt = orderRepository.findById(id);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            if (!"SUCCESS".equals(order.getStatus())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Only successful orders can be refunded.");
                return "redirect:/admin/orders";
            }
            
            com.llbeauty.entity.Payment payment = paymentRepository.findAll().stream()
                .filter(p -> String.valueOf(order.getId()).equals(p.getReferenceId()) && "PRODUCT".equals(p.getPaymentFor()) && "SUCCESS".equals(p.getStatus()))
                .findFirst().orElse(null);

            boolean refundSuccess = false;
            
            if ("WALLET".equalsIgnoreCase(refundMethod)) {
                walletService.credit(order.getUser(), BigDecimal.valueOf(order.getTotalAmount()), "Refund for Order #" + order.getId(), "REFUND");
                refundSuccess = true;
                if (payment != null) {
                    payment.setStatus("REFUNDED_WALLET");
                    paymentRepository.save(payment);
                }
            } else if ("RAZORPAY".equalsIgnoreCase(refundMethod)) {
                if (payment != null && payment.getRazorpayPaymentId() != null) {
                    refundSuccess = paymentService.processRefund(payment.getRazorpayOrderId(), "RAZORPAY", null);
                    if (!refundSuccess) {
                        redirectAttributes.addFlashAttribute("errorMessage", "Razorpay API refund failed.");
                        return "redirect:/admin/orders";
                    }
                } else {
                    redirectAttributes.addFlashAttribute("errorMessage", "No Razorpay payment found for this order.");
                    return "redirect:/admin/orders";
                }
            }

            if (refundSuccess) {
                order.setStatus("REFUNDED");
                orderRepository.save(order);
                redirectAttributes.addFlashAttribute("successMessage", "Order refunded successfully via " + refundMethod);
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Order not found.");
        }
        return "redirect:/admin/orders";
    }

    @GetMapping("/orders/export")
    public void exportOrders(@RequestParam(value = "search", required = false) String search,
                             @RequestParam(value = "status", required = false) String status,
                             HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=orders.csv");
        PrintWriter writer = response.getWriter();
        writer.println("Order ID,Customer Name,Email,Total Amount,Payment ID,Razorpay Order ID,Date,Status");

        List<Order> orders = orderRepository.searchOrdersList(search, status);
        for (Order o : orders) {
            writer.println(String.format("%d,%s,%s,%.2f,%s,%s,%s,%s",
                o.getId(),
                o.getUser() != null ? o.getUser().getName().replace(",", " ") : "Unknown",
                o.getUser() != null ? o.getUser().getEmail() : "",
                o.getTotalAmount(),
                o.getPaymentId() != null ? o.getPaymentId() : "N/A",
                o.getRazorpayOrderId() != null ? o.getRazorpayOrderId() : "N/A",
                o.getCreatedAt(),
                o.getStatus()
            ));
        }
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

    // ==========================================
    //  MANUAL PAYMENT REQUESTS
    // ==========================================
    @GetMapping("/manual-payments")
    public String viewManualPayments(Model model) {
        model.addAttribute("activeTab", "manual-payments");
        model.addAttribute("payments", manualPaymentRequestRepository.findAll());
        return "admin/manual_payments";
    }

    @PostMapping("/manual-payments/{id}/approve")
    public String approveManualPayment(@PathVariable("id") Long id,
                                       @RequestParam(value = "remarks", required = false) String remarks,
                                       RedirectAttributes redirectAttributes) {
        Optional<ManualPaymentRequest> reqOpt = manualPaymentRequestRepository.findById(id);
        if (reqOpt.isPresent()) {
            ManualPaymentRequest req = reqOpt.get();
            req.setStatus("APPROVED");
            if (remarks != null) {
                req.setAdminRemarks(remarks);
            }
            manualPaymentRequestRepository.save(req);

            String purpose = req.getPaymentPurpose();
            User user = req.getUser();
            String refId = req.getReferenceId();

            if ("WALLET_TOPUP".equals(purpose)) {
                walletService.credit(user, BigDecimal.valueOf(req.getAmount()), "Manual Wallet Top-up (UTR: " + req.getUtrNumber() + ")", "MANUAL_TOPUP");
            } else if ("CHECKOUT".equals(purpose) && refId != null) {
                Long orderId = Long.parseLong(refId);
                Order order = orderRepository.findById(orderId).orElse(null);
                if (order != null && "PENDING".equals(order.getStatus())) {
                    order.setStatus("SUCCESS");
                    order.setPaymentId("MANUAL_" + req.getUtrNumber());
                    orderRepository.save(order);
                    
                    Optional<com.llbeauty.entity.UserMembership> activeOpt = membershipService.getActiveMembership(order.getUser());
                    if (activeOpt.isPresent()) {
                        double cashbackPercent = activeOpt.get().getMembership().getCashbackPercent();
                        double cashbackAmount = order.getTotalAmount() * cashbackPercent;
                        if (cashbackAmount > 0) {
                            walletService.credit(order.getUser(), BigDecimal.valueOf(cashbackAmount), "Cashback for Order #" + order.getId() + " (" + activeOpt.get().getMembership().getName() + ")", "CASHBACK");
                        }
                    }
                }
            } else if ("SALON_PAYMENT".equals(purpose) && refId != null) {
                Long appointmentId = Long.parseLong(refId);
                Appointment app = appointmentRepository.findById(appointmentId).orElse(null);
                if (app != null && !"CONFIRMED".equalsIgnoreCase(app.getStatus())) {
                    app.setStatus("CONFIRMED");
                    app.setPaymentStatus("PAID");
                    app.setToken("LL-SLOT-" + (1000 + new java.util.Random().nextInt(9000)));
                    appointmentRepository.save(app);
                }
            }

            redirectAttributes.addFlashAttribute("successMessage", "Payment request approved successfully.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Payment request not found.");
        }
        return "redirect:/admin/manual-payments";
    }

    @PostMapping("/manual-payments/{id}/reject")
    public String rejectManualPayment(@PathVariable("id") Long id,
                                      @RequestParam(value = "remarks", required = false) String remarks,
                                      RedirectAttributes redirectAttributes) {
        Optional<ManualPaymentRequest> reqOpt = manualPaymentRequestRepository.findById(id);
        if (reqOpt.isPresent()) {
            ManualPaymentRequest req = reqOpt.get();
            req.setStatus("REJECTED");
            if (remarks != null) {
                req.setAdminRemarks(remarks);
            }
            manualPaymentRequestRepository.save(req);
            redirectAttributes.addFlashAttribute("successMessage", "Payment request rejected.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Payment request not found.");
        }
        return "redirect:/admin/manual-payments";
    }

    // ==========================================
    //  NOTIFICATIONS SYSTEM
    // ==========================================
    @PostMapping("/notifications/mark-read/{id}")
    public String markNotificationRead(@PathVariable("id") Long id, @RequestParam("redirectUrl") String redirectUrl) {
        notificationService.markAsRead(id);
        return "redirect:" + (redirectUrl != null && !redirectUrl.isEmpty() ? redirectUrl : "/admin/dashboard");
    }

    @PostMapping("/notifications/mark-all-read")
    public String markAllNotificationsRead(RedirectAttributes redirectAttributes) {
        notificationService.markAllAsRead();
        redirectAttributes.addFlashAttribute("successMessage", "All notifications marked as read.");
        return "redirect:/admin/dashboard";
    }
}
