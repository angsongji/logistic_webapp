package com.uteexpress.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Notification entity hợp nhất dùng cho:
 *  - Khách hàng: thông báo trạng thái đơn hàng, thanh toán
 *  - Kế toán: thông báo giao dịch, xác nhận thanh toán
 *  - Shipper, Kho: nhận phân công đơn hàng
 *  - Admin: thông báo hệ thống
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 
     * Người nhận thông báo 
     * VD: CUSTOMER, ACCOUNTANT, WAREHOUSE, SHIPPER, ADMIN 
     */
    @Column(nullable = false)
    private String recipientType;

    /** 
     * ID người nhận trong bảng tương ứng (user id)
     */
    @Column(nullable = false)
    private Long recipientId;

    /** 
     * Tiêu đề thông báo ngắn gọn
     */
    @Column(nullable = false, length = 255)
    private String title;

    /** 
     * Nội dung chi tiết của thông báo
     */
    @Column(nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String message;

    /**
     * Loại thông báo — dùng enum để phân biệt
     * ORDER_CREATED, PAYMENT_CONFIRMED, ORDER_COMPLETED, ORDER_FAILED, v.v.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    /**
     * Liên kết với đơn hàng nếu có
     */
    @ManyToOne
    @JoinColumn(name = "order_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Order order;

    /** 
     * Đánh dấu đã đọc hay chưa
     */
    @Builder.Default
    @Column(name = "is_read")
    private Boolean isRead = false;

    /** 
     * Thời gian tạo thông báo
     */
    @Builder.Default
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (isRead == null) {
            isRead = false;
        }
    }
}
