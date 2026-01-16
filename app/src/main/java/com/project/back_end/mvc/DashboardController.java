package com.project.back_end.mvc;

import com.project.back_end.models.Doctor;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.services.Service;
import com.project.back_end.services.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Map;

@Controller
public class DashboardController {

    private final Service service;
    private final TokenService tokenService;
    private final DoctorRepository doctorRepository;

    public DashboardController(Service service, TokenService tokenService, DoctorRepository doctorRepository) {
        this.service = service;
        this.tokenService = tokenService;
        this.doctorRepository = doctorRepository;
    }

    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@PathVariable String token, Model model) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "admin");
        if (validation == null) {
            model.addAttribute("token", token);
            return "adminDashboard";
        }
        return "redirect:/";
    }

    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboard(@PathVariable String token, Model model) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "doctor");
        if (validation == null) {
            model.addAttribute("token", token);
            try {
                String email = tokenService.extractEmail(token);
                Doctor doctor = doctorRepository.findByEmail(email);
                model.addAttribute("doctorId", doctor != null ? doctor.getId() : 0);
            } catch (Exception e) {
                model.addAttribute("doctorId", 0);
            }
            return "doctorDashboard";
        }
        return "redirect:/";
    }
}
