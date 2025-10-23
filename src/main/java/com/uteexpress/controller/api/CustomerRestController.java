package com.uteexpress.controller.api;

import com.uteexpress.dto.customer.OrderRequestDto;
import com.uteexpress.entity.User;
import com.uteexpress.entity.Order;
import com.uteexpress.entity.Invoice;
import com.uteexpress.entity.Payment;
import com.uteexpress.repository.InvoiceRepository;
import com.uteexpress.repository.PaymentRepository;
import com.uteexpress.service.customer.CustomerService;
import com.uteexpress.service.customer.OrderService;
import com.uteexpress.service.storage.CloudinaryStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/customer")
public class CustomerRestController {

    private final OrderService orderService;
    private final CustomerService customerService;
    private final CloudinaryStorageService cloudinaryStorageService;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;

    public CustomerRestController(OrderService orderService,
                                  CustomerService customerService,
                                  CloudinaryStorageService cloudinaryStorageService,
                                  InvoiceRepository invoiceRepository,
                                  PaymentRepository paymentRepository) {
        this.orderService = orderService;
        this.customerService = customerService;
        this.cloudinaryStorageService = cloudinaryStorageService;
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
    }

    @PostMapping("/orders")
    public ResponseEntity<?> createOrder(
            @RequestBody OrderRequestDto dto,
            Principal principal) {
        User user = customerService.getByUsername(principal.getName());
        try {
            // Debug logging
            System.out.println("=== CREATE ORDER DEBUG ===");
            System.out.println("Principal: " + (principal != null ? principal.getName() : "null"));
            System.out.println("User: " + (user != null ? user.getUsername() : "null"));
            System.out.println("DTO recipientAddress: " + dto.getRecipientAddress());
            System.out.println("DTO recipientName: " + dto.getRecipientName());
            System.out.println("DTO recipientPhone: " + dto.getRecipientPhone());
            System.out.println("DTO senderAddress: " + dto.getSenderAddress());
            System.out.println("DTO imageUrl: " + dto.getImageUrl());
            System.out.println("=========================");
            
            var order = orderService.createOrder(user, dto);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            System.err.println("Error creating order: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error creating order: " + e.getMessage());
        }
    }

    @GetMapping("/orders")
    public ResponseEntity<?> getMyOrders(@RequestParam(value = "status", required = false) String status, Principal principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(401).body("User not authenticated");
            }
            String username = principal.getName();
            System.out.println("getMyOrders requested by user=" + username + ", status=" + status);

            if (status == null || status.isBlank()) {
                var list = orderService.getOrdersByCustomerUsername(username);
                return ResponseEntity.ok(list);
            } else {
                var list = orderService.getOrdersByCustomerUsernameAndStatus(username, status);
                return ResponseEntity.ok(list);
            }
        } catch (Exception e) {
            System.err.println("Error in getMyOrders: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error loading orders: " + e.getMessage());
        }
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

    @PostMapping("/invoices")
    public ResponseEntity<?> createInvoice(@RequestBody Map<String, Object> invoiceData, Principal principal) {
        try {
            System.out.println("=== CREATE INVOICE DEBUG ===");
            System.out.println("Invoice data: " + invoiceData);
            System.out.println("=========================");
            
            // Get orderId from invoice data
            Long orderId = Long.valueOf(invoiceData.get("orderId").toString());
            Order order = orderService.getById(orderId);
            
            // Create invoice
            Invoice invoice = Invoice.builder()
                    .order(order)
                    .invoiceNumber("INV-" + System.currentTimeMillis()) // Generate unique invoice number
                    .totalAmount(new BigDecimal(invoiceData.get("totalAmount").toString()))
                    .taxAmount(new BigDecimal(invoiceData.get("taxAmount").toString()))
                    .discountAmount(new BigDecimal(invoiceData.get("discountAmount").toString()))
                    .finalAmount(new BigDecimal(invoiceData.get("finalAmount").toString()))
                    .notes(invoiceData.get("notes").toString())
                    .status(Invoice.InvoiceStatus.PENDING)
                    .issueDate(LocalDateTime.now())
                    .dueDate(LocalDateTime.now().plusDays(30)) // Due in 30 days
                    .build();
            
            Invoice savedInvoice = invoiceRepository.save(invoice);
            System.out.println("Invoice created with ID: " + savedInvoice.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Invoice created successfully");
            response.put("invoiceId", savedInvoice.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error creating invoice: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error creating invoice: " + e.getMessage());
        }
    }

    @PostMapping("/payments")
    public ResponseEntity<?> createPayment(@RequestBody Map<String, Object> paymentData, Principal principal) {
        try {
            System.out.println("=== CREATE PAYMENT DEBUG ===");
            System.out.println("Payment data: " + paymentData);
            System.out.println("=========================");
            
            // Get orderId from payment data
            Long orderId = Long.valueOf(paymentData.get("orderId").toString());
            Order order = orderService.getById(orderId);
            
            // Create payment
            Payment payment = Payment.builder()
                    .orderRef(order)
                    .amount(new BigDecimal(paymentData.get("amount").toString()))
                    .method(paymentData.get("method").toString())
                    .status("PENDING")
                    .transactionId("TXN-" + System.currentTimeMillis()) // Generate unique transaction ID
                    .build();
            
            Payment savedPayment = paymentRepository.save(payment);
            System.out.println("Payment created with ID: " + savedPayment.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Payment created successfully");
            response.put("paymentId", savedPayment.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error creating payment: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error creating payment: " + e.getMessage());
        }
    }

    @PostMapping("/upload-image")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile image, Principal principal) {
        try {
            System.out.println("Upload image request received");
            System.out.println("Principal: " + (principal != null ? principal.getName() : "null"));
            System.out.println("Image file: " + (image != null ? image.getOriginalFilename() : "null"));
            
            if (principal == null) {
                return ResponseEntity.badRequest().body("User not authenticated");
            }
            
            // Validate file
            if (image == null || image.isEmpty()) {
                System.out.println("No file uploaded");
                return ResponseEntity.badRequest().body("No file uploaded");
            }
            
            System.out.println("File details - Name: " + image.getOriginalFilename() + 
                             ", Size: " + image.getSize() + 
                             ", ContentType: " + image.getContentType());
            
            // Check file type
            String contentType = image.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                System.out.println("Invalid file type: " + contentType);
                return ResponseEntity.badRequest().body("File must be an image");
            }
            
            // Check file size (max 5MB)
            if (image.getSize() > 5 * 1024 * 1024) {
                System.out.println("File too large: " + image.getSize());
                return ResponseEntity.badRequest().body("File size must be less than 5MB");
            }
            
            System.out.println("Uploading to Cloudinary...");
            // Upload to Cloudinary
            String imageUrl = cloudinaryStorageService.uploadFile(image);
            System.out.println("Cloudinary URL: " + imageUrl);
            
            // Return the image URL
            Map<String, String> response = new HashMap<>();
            response.put("imageUrl", imageUrl);
            response.put("message", "Image uploaded successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error uploading image: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error uploading image: " + e.getMessage());
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
