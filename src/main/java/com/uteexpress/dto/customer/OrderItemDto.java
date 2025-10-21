package com.uteexpress.dto.customer;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDto {
    private String name;
    private Integer quantity;
    private BigDecimal unitPrice;
}
