package com.llbeauty.controller;

import com.llbeauty.dto.AdminLoginRequest;
import com.llbeauty.dto.OtpVerifyRequest;
import com.llbeauty.dto.RegisterRequest;
import com.llbeauty.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController {
    
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    public AuthController(final AuthService authService) {
        this.authService = authService;
    }

    // ---- GET LOGIN PAGE ----
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "redirect", required = false) String redirect, Model model) {
        model.addAttribute("redirect", redirect);
        return "login";
    }

    // ---- POST SEND LOGIN OTP ----
    @PostMapping("/login/send-otp")
    public String sendLoginOtp(@RequestParam("email") String email, 
                               @RequestParam(value = "redirect", required = false) String redirect, 
                               RedirectAttributes redirectAttributes) {
        if (email == null || !email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
            redirectAttributes.addFlashAttribute("error", "Enter a valid email address");
            return "redirect:/auth/login" + (redirect != null ? "?redirect=" + redirect : "");
        }
        boolean sent = authService.sendLoginOtp(email);
        if (!sent) {
            redirectAttributes.addFlashAttribute("error", "Email address not registered. Please sign up first.");
            return "redirect:/auth/login" + (redirect != null ? "?redirect=" + redirect : "");
        }
        redirectAttributes.addFlashAttribute("info", "OTP sent successfully! Please check your email.");
        return "redirect:/auth/verify?email=" + email + (redirect != null ? "&redirect=" + redirect : "");
    }

    // ---- GET REGISTER PAGE ----
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";
    }

    // ---- POST REGISTER ----
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerRequest") RegisterRequest request, 
                           BindingResult bindingResult, 
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        boolean registered = authService.registerUser(request);
        if (!registered) {
            bindingResult.rejectValue("email", "duplicate", "Email address is already registered.");
            return "register";
        }
        redirectAttributes.addFlashAttribute("info", "Registration successful! OTP has been sent to your email.");
        return "redirect:/auth/verify?email=" + request.getEmail();
    }

    // ---- GET VERIFY OTP PAGE ----
    @GetMapping("/verify")
    public String verifyPage(@RequestParam("email") String email, 
                             @RequestParam(value = "redirect", required = false) String redirect, 
                             Model model) {
        model.addAttribute("email", email);
        model.addAttribute("redirect", redirect);
        return "verify";
    }

    // ---- POST VERIFY OTP ----
    @PostMapping("/verify")
    public String verifyOtp(@RequestParam("email") String email, 
                            @RequestParam("otp") String otp, 
                            @RequestParam(value = "redirect", required = false) String redirect, 
                            HttpServletResponse response, 
                            RedirectAttributes redirectAttributes) {
        String token = authService.verifyOtpAndLogin(email, otp);
        if (token == null) {
            redirectAttributes.addFlashAttribute("error", "Invalid or expired OTP. Please try again.");
            return "redirect:/auth/verify?email=" + email + (redirect != null ? "&redirect=" + redirect : "");
        }
        
        // Set HttpOnly Cookie for authentication
        Cookie cookie = new Cookie("LLB_TOKEN", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60); // 24 hours
        response.addCookie(cookie);
        
        log.info("User {} successfully logged in.", email);
        if (redirect != null && !redirect.trim().isEmpty() && !redirect.contains("/auth/")) {
            return "redirect:" + redirect;
        }
        return "redirect:/";
    }

    // ---- GET ADMIN LOGIN PAGE ----
    @GetMapping("/admin/login")
    public String adminLoginPage() {
        return "admin_login";
    }

    // ---- POST ADMIN LOGIN ----
    @PostMapping("/admin/login")
    public String adminLogin(@RequestParam("email") String email, 
                             @RequestParam("password") String password, 
                             HttpServletResponse response, 
                             RedirectAttributes redirectAttributes) {
        String token = authService.adminLogin(email, password);
        if (token == null) {
            redirectAttributes.addFlashAttribute("error", "Invalid admin email or password");
            return "redirect:/auth/admin/login";
        }
        
        // Set HttpOnly Cookie for authentication
        Cookie cookie = new Cookie("LLB_TOKEN", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60); // 24 hours
        response.addCookie(cookie);
        
        log.info("Admin {} successfully logged in.", email);
        return "redirect:/admin/dashboard"; // Redirect to admin dashboard
    }
}
