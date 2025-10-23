package com.uteexpress.dto.customer;

import com.uteexpress.entity.Order;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequestDto {
    private String senderName;
    private String senderPhone;
    private String senderAddress;
    private String recipientName;
    private String recipientPhone;
    private String recipientAddress;
    private Order.ServiceType serviceType;
    private BigDecimal shipmentFee;
    private Double weight;
    private Double codAmount;
    private String notes;
    private String imageUrl;
    private List<OrderItemDto> items;
    private MultipartFile image;
}
