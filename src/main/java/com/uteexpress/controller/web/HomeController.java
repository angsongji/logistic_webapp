package com.uteexpress.controller.web;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Authentication authentication) {
        // Check if user is authenticated
        if (authentication != null && authentication.isAuthenticated()) {
            // Get user role and redirect accordingly
            String role = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst()
                    .orElse("ROLE_CUSTOMER");
            
            return getRedirectUrlByRole(role);
        }
        
        // Not authenticated, redirect to login
        return "redirect:/auth/login";
    }

    @GetMapping("/login")
    public String loginRedirect() {
        return "redirect:/auth/login";
    }

    @GetMapping("/register")
    public String registerRedirect() {
        return "redirect:/auth/register";
    }
    
    private String getRedirectUrlByRole(String role) {
        return switch (role) {
            case "ROLE_ACCOUNTANT" -> "redirect:/web/accountant/dashboard";
            case "ROLE_CUSTOMER" -> "redirect:/web/customer/dashboard";
            default -> "redirect:/web/customer/dashboard"; // Default fallback
        };
    }
}
