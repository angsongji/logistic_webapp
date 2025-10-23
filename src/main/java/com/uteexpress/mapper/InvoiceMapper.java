package com.uteexpress.mapper;

import com.uteexpress.dto.customer.InvoiceDto;
import com.uteexpress.entity.Invoice;
import org.springframework.stereotype.Component;

@Component
public class InvoiceMapper {
    
    public static InvoiceDto toDto(Invoice invoice) {
        if (invoice == null) {
            return null;
        }
        
        return InvoiceDto.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .totalAmount(invoice.getTotalAmount())
                .taxAmount(invoice.getTaxAmount())
                .discountAmount(invoice.getDiscountAmount())
                .finalAmount(invoice.getFinalAmount())
                .status(invoice.getStatus() != null ? invoice.getStatus().name() : null)
                .issuedDate(invoice.getIssueDate())
                .dueDate(invoice.getDueDate())
                .paymentDate(invoice.getPaymentDate())
                .notes(invoice.getNotes())
                .createdAt(invoice.getCreatedAt())
                .updatedAt(invoice.getUpdatedAt())
                .build();
    }
}
