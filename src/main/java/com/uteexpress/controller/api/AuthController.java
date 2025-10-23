package com.uteexpress.controller.api;

import com.uteexpress.dto.auth.LoginRequest;
import com.uteexpress.dto.auth.RegisterRequest;
import com.uteexpress.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

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
        return "redirect:/auth/login?success=true";
    }

    @GetMapping("/login")
    public String loginForm(@RequestParam(required = false) String success, Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        if ("true".equals(success)) {
            model.addAttribute("message", "Đăng ký thành công! Vui lòng đăng nhập.");
        }
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginRequest req, Model model, HttpServletResponse response) {
        try {
            // Get JWT token from AuthService
            var result = authService.login(req);
            String token = result.get("token");
            String role = result.get("role");
            
            // Set JWT token as HTTP-only cookie (secure)
            Cookie jwtCookie = new Cookie("jwt_token", token);
            jwtCookie.setHttpOnly(true); // Keep secure
            jwtCookie.setSecure(false); // Set true for HTTPS
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(24 * 60 * 60); // 24 hours
            response.addCookie(jwtCookie);
            
            // Set authentication status cookie for frontend
            Cookie authCookie = new Cookie("is_authenticated", "true");
            authCookie.setHttpOnly(false); // Allow frontend to read
            authCookie.setPath("/");
            authCookie.setMaxAge(24 * 60 * 60);
            response.addCookie(authCookie);
            
            // Set role cookie for frontend display
            Cookie roleCookie = new Cookie("user_role", role);
            roleCookie.setHttpOnly(false); // Allow frontend to read
            roleCookie.setPath("/");
            roleCookie.setMaxAge(24 * 60 * 60);
            response.addCookie(roleCookie);
            
            // Store user info in model
            model.addAttribute("username", req.getUsername());
            model.addAttribute("role", role);
            
            // Redirect based on user role
            return getRedirectUrlByRole(role);
        } catch (RuntimeException ex) {
            model.addAttribute("error", ex.getMessage());
            return "auth/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        // Clear JWT cookie with multiple attempts
        clearCookie(response, "jwt_token", true);
        clearCookie(response, "is_authenticated", false);
        clearCookie(response, "user_role", false);
        
        return "redirect:/auth/login";
    }
    
    @GetMapping("/logout/clear-cookies")
    public String clearCookies(HttpServletResponse response) {
        // Force clear all cookies with different combinations
        clearCookie(response, "jwt_token", true);
        clearCookie(response, "is_authenticated", false);
        clearCookie(response, "user_role", false);
        
        // Additional attempts with different paths
        clearCookieWithPath(response, "jwt_token", true, "/");
        clearCookieWithPath(response, "jwt_token", true, "/auth");
        clearCookieWithPath(response, "is_authenticated", false, "/");
        clearCookieWithPath(response, "user_role", false, "/");
        
        return "redirect:/auth/login";
    }
    
    private void clearCookie(HttpServletResponse response, String cookieName, boolean httpOnly) {
        // For HTTP-only cookies, we need to set them with the same attributes as when they were created
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setHttpOnly(httpOnly);
        cookie.setPath("/");
        cookie.setMaxAge(0); // Expire immediately
        response.addCookie(cookie);
        
        // Also try with null value
        Cookie cookieNull = new Cookie(cookieName, null);
        cookieNull.setHttpOnly(httpOnly);
        cookieNull.setPath("/");
        cookieNull.setMaxAge(0);
        response.addCookie(cookieNull);
    }
    
    private void clearCookieWithPath(HttpServletResponse response, String cookieName, boolean httpOnly, String path) {
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setHttpOnly(httpOnly);
        cookie.setPath(path);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        
        Cookie cookieNull = new Cookie(cookieName, null);
        cookieNull.setHttpOnly(httpOnly);
        cookieNull.setPath(path);
        cookieNull.setMaxAge(0);
        response.addCookie(cookieNull);
    }
    
    private String getRedirectUrlByRole(String role) {
        return switch (role) {
            case "ROLE_ACCOUNTANT" -> "redirect:/web/accountant/dashboard";
            case "ROLE_CUSTOMER" -> "redirect:/web/customer/dashboard";
            default -> "redirect:/web/customer/dashboard"; // Default fallback
        };
    }
}