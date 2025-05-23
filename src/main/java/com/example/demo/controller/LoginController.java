package com.example.demo.controller;

import com.example.demo.model.Admin;
import com.example.demo.model.User;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class LoginController {

    @Autowired
    private AdminRepository adminRepo;
    @Autowired
    private UserRepository userRepo;

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("admin", new Admin());
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@ModelAttribute("admin") Admin adminInput, Model model) {
        Admin admin = adminRepo.findByUsernameAndPassword(adminInput.getUsername(), adminInput.getPassword());

        if (admin != null) {
            List<User> users = (List<User>) userRepo.findAll();
            model.addAttribute("users", users);
            System.out.println("Logged in as admin: " + admin.getUsername());
            return "userlist"; 
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }
    
    @GetMapping("/logout")
    public String logout() {
        return "redirect:/register";
    }


}
