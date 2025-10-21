package com.uteexpress.controller.api;

import com.uteexpress.dto.auth.LoginRequest;
import com.uteexpress.dto.auth.RegisterRequest;
import com.uteexpress.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute RegisterRequest req, Model model) {
        authService.register(req);
        model.addAttribute("message", "Đăng ký thành công. Vui lòng đăng nhập.");
        return "auth/login";
    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginRequest req, Model model) {
        // For Thymeleaf demo we won't generate JWT here; recommend using REST login for JWT
        try {
            authService.login(req);
            return "redirect:/customer/orders/new";
        } catch (RuntimeException ex) {
            model.addAttribute("error", ex.getMessage());
            return "auth/login";
        }
    }
}