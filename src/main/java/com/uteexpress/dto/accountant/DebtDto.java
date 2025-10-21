package com.uteexpress.dto.accountant;

import com.uteexpress.entity.Debt;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DebtDto {
    private Long id;
    private Long debtorId;
    private String debtorName;
    private Long creditorId;
    private String creditorName;
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private BigDecimal remainingAmount;
    private Debt.DebtType debtType;
    private String description;
    private LocalDateTime dueDate;
    private Debt.DebtStatus status;
}
