package com.uteexpress.repository;

import com.uteexpress.entity.FinancialReport;
import com.uteexpress.entity.ReportType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FinancialReportRepository extends JpaRepository<FinancialReport, Long> {
    List<FinancialReport> findByReportTypeOrderByReportDateDesc(ReportType reportType);
    List<FinancialReport> findByReportDateBetween(LocalDate startDate, LocalDate endDate);
    List<FinancialReport> findByReportDateOrderByReportDateDesc(LocalDate reportDate);
}
