package com.uteexpress.dto.customer;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDto {
    private Long id;
    private Long orderId;
    private String orderCode;
    private String customerName;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
