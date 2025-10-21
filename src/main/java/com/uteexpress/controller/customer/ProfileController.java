package com.uteexpress.controller.customer;

import com.uteexpress.entity.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/customer/profile")
public class ProfileController {

    @GetMapping
    public String viewProfile(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("profile", user);
        model.addAttribute("title", "Thông tin cá nhân");
        return "customer/profile";
    }

    @PostMapping
    public String updateProfile(@ModelAttribute User profile, Model model) {
        // You should implement saving via UserService; here just redirect
        model.addAttribute("message", "Đã lưu thông tin (demo)");
        return "redirect:/customer/profile";
    }
}
