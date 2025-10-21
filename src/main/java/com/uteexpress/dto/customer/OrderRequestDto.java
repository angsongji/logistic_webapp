package com.uteexpress.dto.customer;

import com.uteexpress.entity.ServiceType;
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
    private ServiceType serviceType;
    private BigDecimal shipmentFee;
    private String notes;
    private List<OrderItemDto> items;
    private MultipartFile image;
}
