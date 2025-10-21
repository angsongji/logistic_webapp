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
    private String recipientName;
    private BigDecimal totalAmount;
    private BigDecimal shipmentFee;
    private String status;
    private String imageUrl;
    private LocalDateTime createdAt;
    private List<OrderItemDto> items;
}
