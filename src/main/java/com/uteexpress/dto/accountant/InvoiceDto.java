package com.uteexpress.dto.accountant;

import com.uteexpress.entity.Invoice;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceDto {
    private Long id;
    private String invoiceNumber;
    private Long orderId;
    private String orderCode;
    private Long customerId;
    private String customerName;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private String description;
    private Invoice.InvoiceStatus status;
    private LocalDateTime issuedDate;
    private LocalDateTime dueDate;
    private LocalDateTime paidDate;
}
