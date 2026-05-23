package com.llbeauty.controller;

import com.llbeauty.repository.AppointmentRepository;
import com.llbeauty.repository.FranchiseLeadRepository;
import com.llbeauty.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AppointmentRepository appointmentRepository;
    private final FranchiseLeadRepository franchiseLeadRepository;
    private final UserRepository userRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalAppointments", appointmentRepository.count());
        model.addAttribute("totalLeads", franchiseLeadRepository.count());
        model.addAttribute("totalUsers", userRepository.count());
        return "admin/dashboard";
    }

    @GetMapping("/appointments")
    public String viewAppointments(Model model) {
        model.addAttribute("appointments", appointmentRepository.findAll());
        return "admin/appointments";
    }

    @GetMapping("/franchise-leads")
    public String viewFranchiseLeads(Model model) {
        model.addAttribute("leads", franchiseLeadRepository.findAll());
        return "admin/franchise_leads";
    }

    @GetMapping("/users")
    public String viewUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin/users";
    }
}
