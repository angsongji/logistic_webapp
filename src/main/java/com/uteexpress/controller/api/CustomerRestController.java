package com.uteexpress.controller.api;

import com.uteexpress.dto.customer.OrderRequestDto;
import com.uteexpress.entity.User;
import com.uteexpress.service.customer.CustomerService;
import com.uteexpress.service.customer.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/customer")
public class CustomerRestController {

    private final OrderService orderService;
    private final CustomerService customerService;

    public CustomerRestController(OrderService orderService,
                                  CustomerService customerService) {
        this.orderService = orderService;
        this.customerService = customerService;
    }

    @PostMapping("/orders")
    public ResponseEntity<?> createOrder(
            @ModelAttribute OrderRequestDto dto,
            Principal principal) {
        User user = customerService.getByUsername(principal.getName());
        try {
            var order = orderService.createOrder(user, dto);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating order: " + e.getMessage());
        }
    }

    @GetMapping("/orders")
    public ResponseEntity<?> getMyOrders(Principal principal) {
        var list = orderService.getOrdersByCustomerUsername(principal.getName());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/orders/{code}")
    public ResponseEntity<?> getOrderDetail(@PathVariable String code) {
        return ResponseEntity.ok(orderService.getByOrderCode(code));
    }

    @GetMapping("/orders/{code}/tracking")
    public ResponseEntity<?> getOrderTracking(@PathVariable String code, Principal principal) {
        // For now, reuse order detail which contains status; in future extend with timeline
        var dto = orderService.getByOrderCode(code);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Principal principal) {
        try {
            User user = customerService.getByUsername(principal.getName());
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error getting profile: " + e.getMessage());
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody User profile, Principal principal) {
        try {
            User user = customerService.getByUsername(principal.getName());
            // Update user fields
            user.setEmail(profile.getEmail());
            user.setFullName(profile.getFullName());
            user.setPhone(profile.getPhone());
            
            User updatedUser = customerService.updateUser(user);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating profile: " + e.getMessage());
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request, Principal principal) {
        try {
            User user = customerService.getByUsername(principal.getName());
            boolean success = customerService.changePassword(user, request.getCurrentPassword(), request.getNewPassword());
            if (success) {
                return ResponseEntity.ok("Password changed successfully");
            } else {
                return ResponseEntity.badRequest().body("Current password is incorrect");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error changing password: " + e.getMessage());
        }
    }

    // Inner class for change password request
    public static class ChangePasswordRequest {
        private String currentPassword;
        private String newPassword;

        public String getCurrentPassword() { return currentPassword; }
        public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
}
