package com.llbeauty.controller;

import com.llbeauty.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ProductRepository productRepository;

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
    public String salon() {
        return "salon";
    }

    @GetMapping("/franchise")
    public String franchise() {
        return "franchise";
    }
}
