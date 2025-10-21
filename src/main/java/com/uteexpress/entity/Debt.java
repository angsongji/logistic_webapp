package com.uteexpress.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "debts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Debt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "debtor_id")
    private User debtor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creditor_id")
    private User creditor;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column
    private BigDecimal paidAmount;

    @Column
    private BigDecimal remainingAmount;

    @Enumerated(EnumType.STRING)
    private DebtType debtType;

    @Column
    private String description;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Enumerated(EnumType.STRING)
    private DebtStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = DebtStatus.PENDING;
        }
        if (remainingAmount == null) {
            remainingAmount = amount;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        if (remainingAmount == null) {
            remainingAmount = amount.subtract(paidAmount != null ? paidAmount : BigDecimal.ZERO);
        }
    }
}
