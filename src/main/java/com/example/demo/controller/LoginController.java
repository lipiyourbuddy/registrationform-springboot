package com.example.demo.controller;

import com.example.demo.dto.OtpLogin;
import com.example.demo.model.Admin;
import com.example.demo.model.User;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class LoginController {

    @Autowired
    private AdminRepository adminRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private EmailService emailService;

    // Temporary OTP store (email -> OTP)
    private Map<String, String> otpStore = new HashMap<>();

    // Show login page with OTP form
    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("otpLogin", new OtpLogin());
        return "login";
    }

    // Handle "Generate OTP" submission
    @PostMapping("/send-otp")
    public String sendOtp(@ModelAttribute OtpLogin otpLogin, Model model) {
        Admin admin = adminRepo.findByUsername(otpLogin.getUsername());

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if (admin == null || !encoder.matches(otpLogin.getPassword(), admin.getPassword())
                || !admin.getEmail().equals(otpLogin.getEmail())) {
            model.addAttribute("error", "Invalid credentials");
            return "login";
        }

        // Generate and store OTP
        String generatedOtp = String.format("%06d", new Random().nextInt(999999));
        otpStore.put(otpLogin.getEmail(), generatedOtp);

        // Send OTP via email
        emailService.sendOtp(otpLogin.getEmail(), generatedOtp);

        model.addAttribute("otpLogin", otpLogin);
        model.addAttribute("otpSent", true);
        // Do not display OTP on screen anymore
        return "login";
    }

    // Handle OTP verification
    @PostMapping("/verify-otp")
    public String verifyOtp(@ModelAttribute OtpLogin otpLogin, Model model) {
        String correctOtp = otpStore.get(otpLogin.getEmail());

        if (correctOtp != null && correctOtp.equals(otpLogin.getOtp())) {
            List<User> users = (List<User>) userRepo.findAll();
            model.addAttribute("users", users);
            otpStore.remove(otpLogin.getEmail());
            return "userlist";
        } else {
            model.addAttribute("error", "Invalid OTP");
            model.addAttribute("otpSent", true);
            model.addAttribute("otpLogin", otpLogin);
            return "login";
        }
    }

    // Optional logout redirect
    @GetMapping("/logout")
    public String logout() {
        return "redirect:/register";
    }
}
