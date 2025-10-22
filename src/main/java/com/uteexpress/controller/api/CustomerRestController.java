package com.uteexpress.controller.api;

import com.uteexpress.dto.customer.OrderRequestDto;
import com.uteexpress.entity.User;
import com.uteexpress.service.customer.CustomerService;
import com.uteexpress.service.customer.OrderService;
import com.uteexpress.service.storage.CloudinaryStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequestMapping("/api/customer")
public class CustomerRestController {

    private final OrderService orderService;
    private final CustomerService customerService;
    private final CloudinaryStorageService cloudinaryStorageService;

    public CustomerRestController(OrderService orderService,
                                  CustomerService customerService,
                                  CloudinaryStorageService cloudinaryStorageService) {
        this.orderService = orderService;
        this.customerService = customerService;
        this.cloudinaryStorageService = cloudinaryStorageService;
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
        System.out.println("Incoming profile update request: email=" + profile.getEmail()
            + ", fullName=" + profile.getFullName() + ", phone=" + profile.getPhone());
        System.out.println("Existing user before update: email=" + user.getEmail()
            + ", fullName=" + user.getFullName() + ", phone=" + user.getPhone());

        // Update user fields
        user.setEmail(profile.getEmail());
        user.setFullName(profile.getFullName());
        user.setPhone(profile.getPhone());

        User updatedUser = customerService.updateUser(user);
        System.out.println("User after update saved: email=" + updatedUser.getEmail()
            + ", fullName=" + updatedUser.getFullName() + ", phone=" + updatedUser.getPhone());
        return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating profile: " + e.getMessage());
        }
    }

    @PostMapping("/profile/avatar")
    public ResponseEntity<?> uploadAvatar(@RequestParam("avatar") MultipartFile avatar, Principal principal) {
        try {
            System.out.println("Upload avatar request received");
            System.out.println("Principal: " + (principal != null ? principal.getName() : "null"));
            System.out.println("Avatar file: " + (avatar != null ? avatar.getOriginalFilename() : "null"));
            
            if (principal == null) {
                return ResponseEntity.badRequest().body("User not authenticated");
            }
            
            User user = customerService.getByUsername(principal.getName());
            System.out.println("User found: " + user.getUsername());
            
            // Validate file
            if (avatar == null || avatar.isEmpty()) {
                System.out.println("No file uploaded");
                return ResponseEntity.badRequest().body("No file uploaded");
            }
            
            System.out.println("File details - Name: " + avatar.getOriginalFilename() + 
                             ", Size: " + avatar.getSize() + 
                             ", ContentType: " + avatar.getContentType());
            
            // Check file type
            String contentType = avatar.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                System.out.println("Invalid file type: " + contentType);
                return ResponseEntity.badRequest().body("File must be an image");
            }
            
            // Check file size (max 5MB)
            if (avatar.getSize() > 5 * 1024 * 1024) {
                System.out.println("File too large: " + avatar.getSize());
                return ResponseEntity.badRequest().body("File size must be less than 5MB");
            }
            
            System.out.println("Uploading to Cloudinary...");
            // Upload to Cloudinary
            String avatarUrl = cloudinaryStorageService.uploadFile(avatar);
            System.out.println("Cloudinary URL: " + avatarUrl);
            
            // Update user avatar
            user.setAvatarUrl(avatarUrl);
            User updatedUser = customerService.updateUser(user);
            System.out.println("User updated successfully");
            
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            System.err.println("Error uploading avatar: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error uploading avatar: " + e.getMessage());
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
