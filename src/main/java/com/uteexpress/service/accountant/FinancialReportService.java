package com.uteexpress.service.accountant;

import com.uteexpress.dto.accountant.FinancialReportDto;
import com.uteexpress.entity.FinancialReport;

import java.time.LocalDate;
import java.util.List;

public interface FinancialReportService {
    FinancialReport generateReport(FinancialReport.ReportType reportType, LocalDate startDate, LocalDate endDate);
    List<FinancialReport> getAllReports();
    List<FinancialReport> getReportsByType(FinancialReport.ReportType reportType);
    List<FinancialReport> getReportsByDateRange(LocalDate startDate, LocalDate endDate);
    FinancialReport getReportById(Long reportId);
    FinancialReportDto getFinancialSummary(LocalDate startDate, LocalDate endDate);
}
