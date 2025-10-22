package com.uteexpress.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDetailDTO {
    // Invoice info
    private Long id;
    private String invoiceNumber;
    private BigDecimal totalAmount;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private String status;
    private LocalDateTime issuedDate;
    private LocalDateTime dueDate;
    private LocalDateTime paymentDate;
    private String notes;
    private LocalDateTime createdAt;
    
    // Order info
    private Long orderId;
    private String orderCode;
    private String orderStatus;
    private String serviceType;
    private String orderNotes;
    
    // Sender info
    private String senderName;
    private String senderPhone;
    private String senderAddress;
    
    // Recipient info
    private String recipientName;
    private String recipientPhone;
    private String recipientAddress;
}

