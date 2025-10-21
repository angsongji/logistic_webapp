package com.uteexpress.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
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
}
