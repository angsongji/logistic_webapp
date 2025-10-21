package com.uteexpress.dto.accountant;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter
public class PaymentResponseDto {
    private Long id;
    private Long orderId;
    private BigDecimal amount;
    private String method;
    private String status;
    private String transactionId;
    private LocalDateTime createdAt;
}
