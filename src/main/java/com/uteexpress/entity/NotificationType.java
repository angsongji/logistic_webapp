package com.uteexpress.entity;

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
