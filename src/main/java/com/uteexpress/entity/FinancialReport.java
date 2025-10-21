package com.uteexpress.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "financial_reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String reportName;

    @Enumerated(EnumType.STRING)
    private ReportType reportType;

    @Column(name = "report_date", nullable = false)
    private LocalDate reportDate;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private BigDecimal totalRevenue;

    @Column(nullable = false)
    private BigDecimal totalExpenses;

    @Column(nullable = false)
    private BigDecimal netProfit;

    @Column
    private BigDecimal shippingRevenue;

    @Column
    private BigDecimal commissionExpenses;

    @Column
    private BigDecimal operationalExpenses;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // ========== INNER ENUM ==========
    
    public enum ReportType {
        DAILY("Báo cáo hàng ngày"),
        WEEKLY("Báo cáo hàng tuần"),
        MONTHLY("Báo cáo hàng tháng"),
        QUARTERLY("Báo cáo quý"),
        YEARLY("Báo cáo năm"),
        CUSTOM("Báo cáo tùy chỉnh");

        private final String displayName;

        ReportType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
