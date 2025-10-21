package com.uteexpress.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "payrolls")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payroll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private User employee;

    @Column(name = "pay_period_start", nullable = false)
    private LocalDate payPeriodStart;

    @Column(name = "pay_period_end", nullable = false)
    private LocalDate payPeriodEnd;

    @Column(nullable = false)
    private BigDecimal baseSalary;

    @Column
    private BigDecimal overtimePay;

    @Column
    private BigDecimal bonus;

    @Column
    private BigDecimal deduction;

    @Column(nullable = false)
    private BigDecimal netPay;

    @Enumerated(EnumType.STRING)
    private PayrollStatus status;

    @Column(name = "pay_date")
    private LocalDate payDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = PayrollStatus.PENDING;
    }
    
    public enum PayrollStatus {
        PENDING("Chờ duyệt"),
        APPROVED("Đã duyệt"),
        PAID("Đã thanh toán"),
        CANCELLED("Đã hủy");

        private final String displayName;

        PayrollStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
