package com.uteexpress.service.impl;

import com.uteexpress.dto.accountant.FinancialReportDto;
import com.uteexpress.entity.FinancialReport;
import com.uteexpress.repository.FinancialReportRepository;
import com.uteexpress.service.accountant.FinancialReportService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class FinancialReportServiceImpl implements FinancialReportService {

    private final FinancialReportRepository financialReportRepository;

    public FinancialReportServiceImpl(FinancialReportRepository financialReportRepository) {
        this.financialReportRepository = financialReportRepository;
    }

    @Override
    public FinancialReport generateReport(FinancialReport.ReportType reportType, LocalDate startDate, LocalDate endDate) {
        // Implementation will be added later
        return null;
    }

    @Override
    public List<FinancialReport> getAllReports() {
        return financialReportRepository.findAll();
    }

    @Override
    public List<FinancialReport> getReportsByType(FinancialReport.ReportType reportType) {
        return financialReportRepository.findByReportTypeOrderByReportDateDesc(reportType);
    }

    @Override
    public List<FinancialReport> getReportsByDateRange(LocalDate startDate, LocalDate endDate) {
        return financialReportRepository.findByReportDateBetween(startDate, endDate);
    }

    @Override
    public FinancialReport getReportById(Long reportId) {
        return financialReportRepository.findById(reportId).orElse(null);
    }

    @Override
    public FinancialReportDto getFinancialSummary(LocalDate startDate, LocalDate endDate) {
        // Mock data for now
        return FinancialReportDto.builder()
                .totalRevenue(BigDecimal.valueOf(1000000))
                .totalExpenses(BigDecimal.valueOf(600000))
                .netProfit(BigDecimal.valueOf(400000))
                .shippingRevenue(BigDecimal.valueOf(800000))
                .commissionExpenses(BigDecimal.valueOf(100000))
                .operationalExpenses(BigDecimal.valueOf(500000))
                .profitMargin(BigDecimal.valueOf(40.0))
                .build();
    }
}
