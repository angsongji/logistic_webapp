package com.uteexpress.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bank_reconciliations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankReconciliation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String bankName;

    @Column(nullable = false)
    private String accountNumber;

    @Column(name = "reconciliation_date", nullable = false)
    private LocalDate reconciliationDate;

    @Column(name = "statement_balance", nullable = false)
    private BigDecimal statementBalance;

    @Column(name = "book_balance", nullable = false)
    private BigDecimal bookBalance;

    @Column(name = "difference_amount")
    private BigDecimal differenceAmount;

    @Enumerated(EnumType.STRING)
    private ReconciliationStatus status;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = ReconciliationStatus.PENDING;
        if (differenceAmount == null) {
            differenceAmount = statementBalance.subtract(bookBalance);
        }
    }

    // ========== INNER ENUM ==========
    
    public enum ReconciliationStatus {
        PENDING("Chờ đối soát"),
        MATCHED("Đã khớp"),
        DISCREPANCY("Có chênh lệch"),
        RESOLVED("Đã giải quyết");

        private final String displayName;

        ReconciliationStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
