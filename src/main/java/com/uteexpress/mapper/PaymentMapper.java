package com.uteexpress.mapper;

import com.uteexpress.dto.customer.PaymentDto;
import com.uteexpress.entity.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {
    
    public static PaymentDto toDto(Payment payment) {
        if (payment == null) {
            return null;
        }
        
        return PaymentDto.builder()
                .id(payment.getId())
                .amount(payment.getAmount())
                .method(payment.getMethod())
                .status(payment.getStatus())
                .transactionId(payment.getTransactionId())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}
