package com.uteexpress.dto.accountant;

import com.uteexpress.entity.Commission.CommissionStatus;
import com.uteexpress.entity.Commission.CommissionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommissionDto {
    private Long id;
    private Long shipperId;
    private String shipperName;
    private String shipperCode;
    private Long orderId;
    private String orderCode;
    private BigDecimal baseAmount;
    private BigDecimal commissionRate;
    private BigDecimal commissionAmount;
    private CommissionType commissionType;
    private String commissionTypeDisplay;
    private CommissionStatus status;
    private String statusDisplay;
    private LocalDateTime calculatedDate;
    private LocalDateTime paidDate;
    private String notes;
    private LocalDateTime createdAt;
}

