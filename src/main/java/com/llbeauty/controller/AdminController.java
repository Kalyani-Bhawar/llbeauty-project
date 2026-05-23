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
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AppointmentRepository appointmentRepository;
    private final FranchiseLeadRepository franchiseLeadRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final SalonInfoRepository salonInfoRepository;

    public AdminController(AppointmentRepository appointmentRepository,
                           FranchiseLeadRepository franchiseLeadRepository,
                           UserRepository userRepository,
                           ProductRepository productRepository,
                           SalonInfoRepository salonInfoRepository) {
        this.appointmentRepository = appointmentRepository;
        this.franchiseLeadRepository = franchiseLeadRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.salonInfoRepository = salonInfoRepository;
    }

    // ==========================================
    //  DASHBOARD
    // ==========================================
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("activeTab", "dashboard");
        model.addAttribute("totalAppointments", appointmentRepository.count());
        model.addAttribute("totalLeads", franchiseLeadRepository.count());
        model.addAttribute("totalUsers", userRepository.count());
        model.addAttribute("totalProducts", productRepository.count());
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
            user.setWalletBalance(balance);
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
}
