package com.uteexpress.controller.api;

import com.uteexpress.dto.DashboardSummaryDTO;
import com.uteexpress.dto.InvoiceDTO;
import com.uteexpress.dto.InvoiceDetailDTO;
import com.uteexpress.dto.PaymentDTO;
import com.uteexpress.dto.accountant.CommissionDto;
import com.uteexpress.dto.accountant.FinancialReportDto;
import com.uteexpress.entity.Commission.CommissionStatus;
import com.uteexpress.entity.Debt;
import com.uteexpress.entity.FinancialReport;
import com.uteexpress.entity.Invoice;
import com.uteexpress.entity.Order;
import com.uteexpress.entity.Payment;
import com.uteexpress.entity.User;
import com.uteexpress.repository.*;
import com.uteexpress.service.accountant.CommissionService;
import com.uteexpress.service.accountant.FinancialReportService;
import com.uteexpress.service.accountant.PaymentService;
import com.uteexpress.service.customer.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/accountant")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ACCOUNTANT') or hasRole('ADMIN')")
public class AccountantRestController {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final DebtRepository debtRepository;
    private final CommissionService commissionService;
    private final FinancialReportService financialReportService;
    private final PaymentService paymentService;
    private final CustomerService customerService;

    // ================================================================
    // DASHBOARD SUMMARY
    // ================================================================
    @GetMapping("/dashboard/summary")
    public ResponseEntity<DashboardSummaryDTO> getDashboardSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        // Get all payments (filter by date if needed)
        List<Payment> allPayments;
        List<Order> orders;
        
        if (startDate == null && endDate == null) {
            allPayments = paymentRepository.findAll();
            orders = orderRepository.findAll();
        } else {
            // Default to current month if only one param provided
            if (startDate == null) {
                startDate = LocalDate.now().withDayOfMonth(1);
            }
            if (endDate == null) {
                endDate = LocalDate.now();
            }
            
            LocalDateTime start = startDate.atStartOfDay();
            LocalDateTime end = endDate.atTime(LocalTime.MAX);
            
            // Filter payments by date
            allPayments = paymentRepository.findAll().stream()
                    .filter(p -> p.getCreatedAt() != null && 
                                 p.getCreatedAt().isAfter(start) && 
                                 p.getCreatedAt().isBefore(end))
                    .collect(Collectors.toList());
            
            orders = orderRepository.findByCreatedAtBetween(start, end);
        }

        // Calculate total revenue from COMPLETED PAYMENTS ONLY
        BigDecimal totalRevenue = allPayments.stream()
                .filter(p -> "COMPLETED".equals(p.getStatus()))
                .map(Payment::getAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate total expense from debts
        List<Debt> debts = debtRepository.findAll();
        BigDecimal totalExpense = debts.stream()
                .filter(d -> d.getStatus() == Debt.DebtStatus.PENDING || d.getStatus() == Debt.DebtStatus.OVERDUE)
                .map(Debt::getRemainingAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate orders statistics
        long totalOrders = orders.size();
        long completedOrders = orders.stream()
                .filter(o -> Order.OrderStatus.HOAN_THANH.equals(o.getStatus()))
                .count();
        long pendingOrders = orders.stream()
                .filter(o -> Order.OrderStatus.CHO_GIAO.equals(o.getStatus()))
                .count();

        DashboardSummaryDTO summary = DashboardSummaryDTO.builder()
                .totalRevenue(totalRevenue)
                .totalExpense(totalExpense)
                .netProfit(totalRevenue.subtract(totalExpense))
                .totalOrders(totalOrders)
                .completedOrders(completedOrders)
                .pendingOrders(pendingOrders)
                .build();

        return ResponseEntity.ok(summary);
    }

    // ================================================================
    // PAYMENTS
    // ================================================================
    @GetMapping("/payments")
    public ResponseEntity<List<PaymentDTO>> getAllPayments() {
        List<Payment> payments = paymentRepository.findAll();
        List<PaymentDTO> paymentDTOs = payments.stream()
                .map(this::convertToPaymentDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(paymentDTOs);
    }

    @GetMapping("/payments/status/{status}")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByStatus(@PathVariable String status) {
        List<Payment> payments = paymentRepository.findByStatus(status);
        List<PaymentDTO> paymentDTOs = payments.stream()
                .map(this::convertToPaymentDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(paymentDTOs);
    }

    @GetMapping("/payments/method/{method}")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByMethod(@PathVariable String method) {
        List<Payment> payments = paymentService.getPaymentsByMethod(method);
        List<PaymentDTO> paymentDTOs = payments.stream()
                .map(this::convertToPaymentDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(paymentDTOs);
    }

    @GetMapping("/payments/date-range")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Payment> payments = paymentService.getPaymentsByDateRange(startDate, endDate);
        List<PaymentDTO> paymentDTOs = payments.stream()
                .map(this::convertToPaymentDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(paymentDTOs);
    }

    @GetMapping("/payments/today")
    public ResponseEntity<List<PaymentDTO>> getTodayPayments() {
        // Get payments from last 30 days (more useful than just today)
        LocalDateTime last30Days = LocalDateTime.now().minusDays(30);
        
        List<Payment> payments = paymentRepository.findAll().stream()
                .filter(p -> p.getCreatedAt() != null && p.getCreatedAt().isAfter(last30Days))
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt())) // Newest first
                .collect(Collectors.toList());
        
        List<PaymentDTO> paymentDTOs = payments.stream()
                .map(this::convertToPaymentDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(paymentDTOs);
    }

    @PostMapping("/payments/{id}/confirm")
    public ResponseEntity<String> confirmPayment(@PathVariable Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        payment.setStatus("COMPLETED");
        paymentRepository.save(payment);
        
        return ResponseEntity.ok("Payment confirmed successfully");
    }

    @PostMapping("/payments/{id}/refund")
    public ResponseEntity<String> refundPayment(@PathVariable Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        payment.setStatus("REFUNDED");
        paymentRepository.save(payment);
        
        return ResponseEntity.ok("Payment refunded successfully");
    }

    // ================================================================
    // INVOICES
    // ================================================================
    @GetMapping("/invoices")
    public ResponseEntity<List<InvoiceDTO>> getAllInvoices() {
        List<Invoice> invoices = invoiceRepository.findAll();
        List<InvoiceDTO> invoiceDTOs = invoices.stream()
                .map(this::convertToInvoiceDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(invoiceDTOs);
    }

    @GetMapping("/invoices/status/{status}")
    public ResponseEntity<List<InvoiceDTO>> getInvoicesByStatus(@PathVariable String status) {
        Invoice.InvoiceStatus invoiceStatus;
        try {
            invoiceStatus = Invoice.InvoiceStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        
        List<Invoice> invoices = invoiceRepository.findByStatusOrderByIssueDateDesc(invoiceStatus);
        List<InvoiceDTO> invoiceDTOs = invoices.stream()
                .map(this::convertToInvoiceDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(invoiceDTOs);
    }

    @GetMapping("/invoices/pending")
    public ResponseEntity<List<InvoiceDTO>> getPendingInvoices() {
        List<Invoice> invoices = invoiceRepository.findByStatusOrderByIssueDateDesc(Invoice.InvoiceStatus.PENDING);
        List<InvoiceDTO> invoiceDTOs = invoices.stream()
                .map(this::convertToInvoiceDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(invoiceDTOs);
    }

    @GetMapping("/invoices/{id}")
    public ResponseEntity<InvoiceDetailDTO> getInvoiceById(@PathVariable Long id) {
        return invoiceRepository.findById(id)
                .map(this::convertToInvoiceDetailDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ================================================================
    // DEBTS
    // ================================================================
    @GetMapping("/debts/overdue")
    public ResponseEntity<Long> getOverdueDebtsCount() {
        List<Debt> debts = debtRepository.findByStatus(Debt.DebtStatus.OVERDUE);
        return ResponseEntity.ok((long) debts.size());
    }

    @GetMapping("/debts/total")
    public ResponseEntity<BigDecimal> getTotalDebt() {
        List<Debt> debts = debtRepository.findAll();
        BigDecimal totalDebt = debts.stream()
                .filter(d -> d.getStatus() != Debt.DebtStatus.PAID)
                .map(Debt::getRemainingAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return ResponseEntity.ok(totalDebt);
    }

    // ================================================================
    // FINANCIAL REPORTS (Báo cáo tài chính)
    // ================================================================
    @GetMapping("/reports")
    public ResponseEntity<List<FinancialReport>> getAllFinancialReports() {
        List<FinancialReport> reports = financialReportService.getAllReports();
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/reports/{id}")
    public ResponseEntity<FinancialReport> getFinancialReportById(@PathVariable Long id) {
        try {
            FinancialReport report = financialReportService.getReportById(id);
            return ResponseEntity.ok(report);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/reports/type/{type}")
    public ResponseEntity<List<FinancialReport>> getReportsByType(@PathVariable String type) {
        try {
            FinancialReport.ReportType reportType = FinancialReport.ReportType.valueOf(type);
            List<FinancialReport> reports = financialReportService.getReportsByType(reportType);
            return ResponseEntity.ok(reports);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/reports/generate")
    public ResponseEntity<FinancialReport> generateFinancialReport(
            @RequestParam String reportType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            FinancialReport.ReportType type = FinancialReport.ReportType.valueOf(reportType);
            FinancialReport report = financialReportService.generateReport(type, startDate, endDate);
            return ResponseEntity.ok(report);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/reports/summary")
    public ResponseEntity<DashboardSummaryDTO> getReportSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        return getDashboardSummary(startDate, endDate);
    }

    @GetMapping("/reports/financial-summary")
    public ResponseEntity<FinancialReportDto> getFinancialSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        FinancialReportDto summary = financialReportService.getFinancialSummary(startDate, endDate);
        return ResponseEntity.ok(summary);
    }

    // ================================================================
    // COMMISSIONS (Quản lý hoa hồng shipper)
    // ================================================================
    @GetMapping("/commissions")
    public ResponseEntity<List<CommissionDto>> getAllCommissions() {
        List<CommissionDto> commissions = commissionService.getAllCommissions();
        return ResponseEntity.ok(commissions);
    }

    @GetMapping("/commissions/{id}")
    public ResponseEntity<CommissionDto> getCommissionById(@PathVariable Long id) {
        try {
            CommissionDto commission = commissionService.getCommissionById(id);
            return ResponseEntity.ok(commission);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/commissions/shipper/{shipperId}")
    public ResponseEntity<List<CommissionDto>> getCommissionsByShipper(@PathVariable Long shipperId) {
        List<CommissionDto> commissions = commissionService.getCommissionsByShipper(shipperId);
        return ResponseEntity.ok(commissions);
    }

    @GetMapping("/commissions/status/{status}")
    public ResponseEntity<List<CommissionDto>> getCommissionsByStatus(@PathVariable String status) {
        try {
            CommissionStatus commissionStatus = CommissionStatus.valueOf(status);
            List<CommissionDto> commissions = commissionService.getCommissionsByStatus(commissionStatus);
            return ResponseEntity.ok(commissions);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/commissions/pending")
    public ResponseEntity<List<CommissionDto>> getPendingCommissions() {
        List<CommissionDto> commissions = commissionService.getCommissionsByStatus(CommissionStatus.PENDING);
        return ResponseEntity.ok(commissions);
    }

    @GetMapping("/commissions/date-range")
    public ResponseEntity<List<CommissionDto>> getCommissionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<CommissionDto> commissions = commissionService.getCommissionsByDateRange(startDate, endDate);
        return ResponseEntity.ok(commissions);
    }

    @PostMapping("/commissions/{id}/approve")
    public ResponseEntity<CommissionDto> approveCommission(@PathVariable Long id) {
        try {
            CommissionDto commission = commissionService.approveCommission(id);
            return ResponseEntity.ok(commission);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/commissions/{id}/pay")
    public ResponseEntity<CommissionDto> payCommission(@PathVariable Long id) {
        try {
            CommissionDto commission = commissionService.payCommission(id);
            return ResponseEntity.ok(commission);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/commissions/{id}/cancel")
    public ResponseEntity<CommissionDto> cancelCommission(@PathVariable Long id) {
        try {
            CommissionDto commission = commissionService.cancelCommission(id);
            return ResponseEntity.ok(commission);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/commissions/{id}")
    public ResponseEntity<CommissionDto> updateCommission(
            @PathVariable Long id, 
            @RequestBody CommissionDto updateData) {
        try {
            CommissionDto commission = commissionService.updateCommission(id, updateData);
            return ResponseEntity.ok(commission);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/commissions/shipper/{shipperId}/pending-total")
    public ResponseEntity<Double> getTotalPendingCommission(@PathVariable Long shipperId) {
        Double total = commissionService.getTotalPendingCommission(shipperId);
        return ResponseEntity.ok(total);
    }

    // ================================================================
    // PROFILE
    // ================================================================
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Principal principal) {
        try {
            User user = customerService.getByUsername(principal.getName());
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error getting profile: " + e.getMessage());
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody User profile, Principal principal) {
        try {
            User user = customerService.getByUsername(principal.getName());
            
            // Update user fields
            user.setEmail(profile.getEmail());
            user.setFullName(profile.getFullName());
            user.setPhone(profile.getPhone());

            User updatedUser = customerService.updateUser(user);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating profile: " + e.getMessage());
        }
    }

    // ================================================================
    // HELPER METHODS
    // ================================================================
    private PaymentDTO convertToPaymentDTO(Payment payment) {
        return PaymentDTO.builder()
                .id(payment.getId())
                .orderId(payment.getOrderRef() != null ? payment.getOrderRef().getId() : null)
                .orderCode(payment.getOrderRef() != null ? payment.getOrderRef().getOrderCode() : null)
                .amount(payment.getAmount())
                .paymentMethod(payment.getMethod())
                .transactionId(payment.getTransactionId())
                .status(payment.getStatus())
                .paymentDate(null) // Payment entity không có paymentDate
                .createdAt(payment.getCreatedAt())
                .build();
    }

    private InvoiceDTO convertToInvoiceDTO(Invoice invoice) {
        return InvoiceDTO.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .orderId(invoice.getOrder() != null ? invoice.getOrder().getId() : null)
                .orderCode(invoice.getOrder() != null ? invoice.getOrder().getOrderCode() : null)
                .totalAmount(invoice.getTotalAmount())
                .taxAmount(invoice.getTaxAmount())
                .discountAmount(invoice.getDiscountAmount())
                .finalAmount(invoice.getFinalAmount())
                .status(invoice.getStatus() != null ? invoice.getStatus().name() : null)
                .issuedDate(invoice.getIssueDate())
                .dueDate(invoice.getDueDate())
                .createdAt(invoice.getCreatedAt())
                .build();
    }

    private InvoiceDetailDTO convertToInvoiceDetailDTO(Invoice invoice) {
        Order order = invoice.getOrder();
        
        return InvoiceDetailDTO.builder()
                // Invoice info
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .totalAmount(invoice.getTotalAmount())
                .taxAmount(invoice.getTaxAmount())
                .discountAmount(invoice.getDiscountAmount())
                .finalAmount(invoice.getFinalAmount())
                .status(invoice.getStatus() != null ? invoice.getStatus().name() : null)
                .issuedDate(invoice.getIssueDate())
                .dueDate(invoice.getDueDate())
                .paymentDate(invoice.getPaymentDate())
                .notes(invoice.getNotes())
                .createdAt(invoice.getCreatedAt())
                // Order info
                .orderId(order != null ? order.getId() : null)
                .orderCode(order != null ? order.getOrderCode() : null)
                .orderStatus(order != null && order.getStatus() != null ? order.getStatus().name() : null)
                .serviceType(order != null && order.getServiceType() != null ? order.getServiceType().name() : null)
                .orderNotes(order != null ? order.getNotes() : null)
                // Sender info
                .senderName(order != null ? order.getSenderName() : null)
                .senderPhone(order != null ? order.getSenderPhone() : null)
                .senderAddress(order != null ? order.getSenderAddress() : null)
                // Recipient info
                .recipientName(order != null ? order.getRecipientName() : null)
                .recipientPhone(order != null ? order.getRecipientPhone() : null)
                .recipientAddress(order != null ? order.getRecipientAddress() : null)
                .build();
    }
}
