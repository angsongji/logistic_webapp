package com.uteexpress.dto.customer;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDto {
    private String orderCode;
    private String senderName;
    private String senderPhone;
    private String senderAddress;
    private String recipientName;
    private String recipientPhone;
    private String recipientAddress;
    private BigDecimal totalAmount;
    private BigDecimal shipmentFee;
    private BigDecimal codAmount;
    private BigDecimal weight;
    private String status;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String notes;
    private List<OrderItemDto> items;
    private String serviceType;
    private Long customerId;
    private Long shipperId;
    private LocalDateTime pickupDate;
    private LocalDateTime deliveryDate;
    
    // Nested objects
    private InvoiceDto invoice;
    private PaymentDto payment;
}
