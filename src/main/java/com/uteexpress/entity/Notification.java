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
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (isRead == null) isRead = false;
    }

    // ========== INNER ENUM ==========
    
    public enum NotificationType {
        ORDER_CREATED,           // Khách hàng hoặc kế toán nhận khi có đơn mới
        ORDER_ASSIGNED,          // Shipper hoặc kho được giao đơn
        ORDER_SHIPPING,          // Đơn hàng đang được giao
        ORDER_COMPLETED,         // Đơn hoàn tất
        ORDER_FAILED,            // Đơn bị hủy hoặc thất bại

        PAYMENT_PENDING,         // Kế toán được báo có giao dịch chờ xử lý
        PAYMENT_CONFIRMED,       // Khách hàng nhận khi kế toán xác nhận
        PAYMENT_FAILED,          // Thanh toán không thành công
        REFUND_PROCESSED,        // Kế toán xác nhận hoàn tiền
        INFO,

        NEW_DELIVERY,            // Shipper nhận đơn mới
        DELIVERY_COMPLETED,      // Shipper hoặc khách hàng nhận khi giao xong

        SYSTEM_ALERT,            // Cảnh báo nội bộ (cho admin / kế toán)
        REPORT_READY             // Kế toán nhận khi báo cáo doanh thu đã sẵn sàng
    }
}
