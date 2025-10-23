package com.uteexpress.service.impl;

import com.uteexpress.dto.accountant.FinancialReportDto;
import com.uteexpress.entity.FinancialReport;
import com.uteexpress.entity.Order;
import com.uteexpress.entity.Payment;
import com.uteexpress.repository.FinancialReportRepository;
import com.uteexpress.repository.OrderRepository;
import com.uteexpress.repository.PaymentRepository;
import com.uteexpress.repository.CommissionRepository;
import com.uteexpress.service.accountant.FinancialReportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FinancialReportServiceImpl implements FinancialReportService {

    private final FinancialReportRepository financialReportRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final CommissionRepository commissionRepository;

    public FinancialReportServiceImpl(FinancialReportRepository financialReportRepository,
                                     OrderRepository orderRepository,
                                     PaymentRepository paymentRepository,
                                     CommissionRepository commissionRepository) {
        this.financialReportRepository = financialReportRepository;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.commissionRepository = commissionRepository;
    }

    @Override
    @Transactional
    public FinancialReport generateReport(FinancialReport.ReportType reportType, LocalDate startDate, LocalDate endDate) {
        // Convert to LocalDateTime for queries
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        
        // Get orders in date range
        List<Order> orders = orderRepository.findByCreatedAtBetween(startDateTime, endDateTime);
        
        // Get payments in date range
        List<Payment> payments = paymentRepository.findByCreatedAtBetween(startDateTime, endDateTime);
        
        // Calculate total revenue from completed payments
        BigDecimal totalRevenue = payments.stream()
                .filter(p -> "COMPLETED".equals(p.getStatus()))
                .map(Payment::getAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Calculate shipping revenue (from completed orders)
        BigDecimal shippingRevenue = orders.stream()
                .filter(o -> Order.OrderStatus.HOAN_THANH.equals(o.getStatus()))
                .map(Order::getShipmentFee)
                .filter(fee -> fee != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Calculate commission expenses - CHỈ tính hoa hồng từ orders trong kỳ
        List<Long> orderIdsInPeriod = orders.stream()
                .map(Order::getId)
                .collect(Collectors.toList());
        
        BigDecimal commissionExpenses = commissionRepository.findByCalculatedDateBetween(startDate, endDate)
                .stream()
                .filter(c -> c.getOrder() != null && orderIdsInPeriod.contains(c.getOrder().getId()))
                .map(c -> c.getCommissionAmount())
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Calculate operational expenses - Cải thiện logic
        // Nếu doanh thu < 1 triệu: Chi phí cố định 200k
        // Nếu doanh thu >= 1 triệu: 20% doanh thu
        BigDecimal operationalExpenses;
        if (totalRevenue.compareTo(new BigDecimal("1000000")) < 0) {
            // Doanh thu thấp - chi phí cố định
            operationalExpenses = new BigDecimal("200000");
        } else {
            // Doanh thu cao - tính theo tỷ lệ 20%
            operationalExpenses = totalRevenue
                    .multiply(new BigDecimal("0.20"))
                    .setScale(2, RoundingMode.HALF_UP);
        }
        
        // Total expenses
        BigDecimal totalExpenses = commissionExpenses.add(operationalExpenses);
        
        // Net profit
        BigDecimal netProfit = totalRevenue.subtract(totalExpenses);
        
        // Count total orders in period (all statuses)
        int totalOrders = orders.size();
        
        // Generate report name
        String reportName = generateReportName(reportType, startDate, endDate);
        
        // Create and save report
        FinancialReport report = FinancialReport.builder()
                .reportName(reportName)
                .reportType(reportType)
                .reportDate(LocalDate.now())
                .startDate(startDate)
                .endDate(endDate)
                .totalRevenue(totalRevenue)
                .totalExpenses(totalExpenses)
                .netProfit(netProfit)
                .shippingRevenue(shippingRevenue)
                .commissionExpenses(commissionExpenses)
                .operationalExpenses(operationalExpenses)
                .totalOrders(totalOrders)
                .build();
        
        return financialReportRepository.save(report);
    }
    
    private String generateReportName(FinancialReport.ReportType reportType, LocalDate startDate, LocalDate endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        switch (reportType) {
            case DAILY:
                return "Báo cáo ngày " + startDate.format(formatter);
            case WEEKLY:
                return "Báo cáo tuần từ " + startDate.format(formatter) + " đến " + endDate.format(formatter);
            case MONTHLY:
                return "Báo cáo tháng " + startDate.getMonthValue() + "/" + startDate.getYear();
            case QUARTERLY:
                int quarter = (startDate.getMonthValue() - 1) / 3 + 1;
                return "Báo cáo quý " + quarter + "/" + startDate.getYear();
            case YEARLY:
                return "Báo cáo năm " + startDate.getYear();
            default:
                return "Báo cáo từ " + startDate.format(formatter) + " đến " + endDate.format(formatter);
        }
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
        // Convert to LocalDateTime for queries
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        
        // Get orders in date range
        List<Order> orders = orderRepository.findByCreatedAtBetween(startDateTime, endDateTime);
        
        // Get payments in date range
        List<Payment> payments = paymentRepository.findByCreatedAtBetween(startDateTime, endDateTime);
        
        // Calculate total revenue from completed payments
        BigDecimal totalRevenue = payments.stream()
                .filter(p -> "COMPLETED".equals(p.getStatus()))
                .map(Payment::getAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Calculate shipping revenue (from completed orders)
        BigDecimal shippingRevenue = orders.stream()
                .filter(o -> Order.OrderStatus.HOAN_THANH.equals(o.getStatus()))
                .map(Order::getShipmentFee)
                .filter(fee -> fee != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Calculate commission expenses - CHỈ tính hoa hồng từ orders trong kỳ
        List<Long> orderIdsInPeriod = orders.stream()
                .map(Order::getId)
                .collect(Collectors.toList());
        
        BigDecimal commissionExpenses = BigDecimal.ZERO;
        if (!orderIdsInPeriod.isEmpty()) {
            commissionExpenses = commissionRepository.findByCalculatedDateBetween(startDate, endDate)
                    .stream()
                    .filter(c -> c.getOrder() != null && orderIdsInPeriod.contains(c.getOrder().getId()))
                    .map(c -> c.getCommissionAmount())
                    .filter(amount -> amount != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        
        // Calculate operational expenses
        BigDecimal operationalExpenses;
        if (totalRevenue.compareTo(new BigDecimal("1000000")) < 0) {
            operationalExpenses = new BigDecimal("200000");
        } else {
            operationalExpenses = totalRevenue
                    .multiply(new BigDecimal("0.20"))
                    .setScale(2, RoundingMode.HALF_UP);
        }
        
        // Total expenses
        BigDecimal totalExpenses = commissionExpenses.add(operationalExpenses);
        
        // Net profit
        BigDecimal netProfit = totalRevenue.subtract(totalExpenses);
        
        // Profit margin (%)
        BigDecimal profitMargin = BigDecimal.ZERO;
        if (totalRevenue.compareTo(BigDecimal.ZERO) > 0) {
            profitMargin = netProfit
                    .multiply(new BigDecimal("100"))
                    .divide(totalRevenue, 2, RoundingMode.HALF_UP);
        }
        
        return FinancialReportDto.builder()
                .totalRevenue(totalRevenue)
                .totalExpenses(totalExpenses)
                .netProfit(netProfit)
                .shippingRevenue(shippingRevenue)
                .commissionExpenses(commissionExpenses)
                .operationalExpenses(operationalExpenses)
                .profitMargin(profitMargin)
                .build();
    }
}
