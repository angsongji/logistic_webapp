package com.uteexpress.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "commissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Commission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipper_id")
    private Shipper shipper;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column
    private BigDecimal percentage;

    @Enumerated(EnumType.STRING)
    private CommissionType commissionType;

    @Enumerated(EnumType.STRING)
    private CommissionStatus status;

    @Column(name = "calculated_date", nullable = false)
    private LocalDateTime calculatedDate;

    @Column(name = "paid_date")
    private LocalDateTime paidDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        calculatedDate = LocalDateTime.now();
        if (status == null) {
            status = CommissionStatus.PENDING;
        }
    }
}
