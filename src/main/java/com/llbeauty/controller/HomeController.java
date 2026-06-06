package com.llbeauty.controller;

import com.llbeauty.repository.ProductRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    private final ProductRepository productRepository;
    private final com.llbeauty.repository.SalonServiceRepository salonServiceRepository;

    public HomeController(final ProductRepository productRepository,
                          final com.llbeauty.repository.SalonServiceRepository salonServiceRepository) {
        this.productRepository = productRepository;
        this.salonServiceRepository = salonServiceRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("products", productRepository.findAll());
        return "index";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/salon")
    public String salon(Model model) {
        model.addAttribute("services", salonServiceRepository.findAll());
        return "salon";
    }

    @GetMapping("/franchise")
    public String franchise() {
        return "franchise";
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }
}
