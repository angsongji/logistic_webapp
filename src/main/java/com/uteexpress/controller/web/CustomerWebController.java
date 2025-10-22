package com.uteexpress.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CustomerWebController {

    @GetMapping("/web/customer")
    public String customerShell(Model model) {
        model.addAttribute("title", "Khu vực khách hàng");
        return "customer/index";
    }

    @GetMapping("/web/customer/dashboard")
    public String customerDashboard(Model model) {
        model.addAttribute("title", "Dashboard Khách hàng");
        return "customer/dashboard";
    }

    @GetMapping("/web/customer/orders/new")
    public String createOrderPage(Model model) {
        model.addAttribute("title", "Tạo đơn hàng");
        return "customer/order_form";
    }

    @GetMapping("/web/customer/addresses")
    public String addressesPage(Model model) {
        model.addAttribute("title", "Quản lý địa chỉ");
        return "customer/addresses";
    }

    @GetMapping("/web/customer/chat")
    public String chatPage(Model model) {
        model.addAttribute("title", "Chat hỗ trợ");
        return "customer/chat";
    }

    @GetMapping("/web/customer/reviews")
    public String reviewsPage(Model model) {
        model.addAttribute("title", "Đánh giá dịch vụ");
        return "customer/reviews";
    }

    @GetMapping("/web/customer/tracking")
    public String trackingPage(Model model) {
        model.addAttribute("title", "Theo dõi đơn hàng");
        return "customer/tracking";
    }

    @GetMapping("/web/customer/profile")
    public String profilePage(Model model, @org.springframework.web.bind.annotation.RequestHeader(value = "HX-Request", required = false) String hxRequest) {
        model.addAttribute("title", "Tài khoản của tôi");
        // If request comes from HTMX, return only the fragment to be inserted into the page
        if (hxRequest != null && hxRequest.equalsIgnoreCase("true")) {
            return "customer/profile :: profile-content";
        }
        return "customer/profile";
    }
}
