package com.uteexpress.dto.accountant;

import com.uteexpress.entity.FinancialReport;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialReportDto {
    private Long id;
    private String reportName;
    private FinancialReport.ReportType reportType;
    private LocalDate reportDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalRevenue;
    private BigDecimal totalExpenses;
    private BigDecimal netProfit;
    private BigDecimal shippingRevenue;
    private BigDecimal commissionExpenses;
    private BigDecimal operationalExpenses;
    private BigDecimal profitMargin;
}
