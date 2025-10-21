package com.uteexpress.controller.api;

import com.uteexpress.dto.accountant.DebtDto;
import com.uteexpress.dto.accountant.FinancialReportDto;
import com.uteexpress.dto.accountant.InvoiceDto;
import com.uteexpress.entity.Payment;
import com.uteexpress.service.accountant.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/accountant")
public class AccountantRestController {

    private final PaymentService paymentService;
    private final InvoiceService invoiceService;
    private final FinancialReportService financialReportService;
    private final DebtService debtService;

    public AccountantRestController(PaymentService paymentService,
                                   InvoiceService invoiceService,
                                   FinancialReportService financialReportService,
                                   DebtService debtService) {
        this.paymentService = paymentService;
        this.invoiceService = invoiceService;
        this.financialReportService = financialReportService;
        this.debtService = debtService;
    }

    @PostMapping("/payments/{orderId}/create")
    public ResponseEntity<Payment> createPayment(
            @PathVariable Long orderId,
            @RequestParam BigDecimal amount,
            @RequestParam String method) {
        return ResponseEntity.ok(paymentService.createPayment(orderId, amount, method));
    }

    @GetMapping("/payments")
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @PostMapping("/payments/{id}/confirm")
    public ResponseEntity<Payment> confirmPayment(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.confirmPayment(id));
    }

    @PostMapping("/payments/{id}/refund")
    public ResponseEntity<Payment> refund(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.refund(id));
    }

    // Invoice Management
    @PostMapping("/invoices")
    public ResponseEntity<com.uteexpress.entity.Invoice> createInvoice(@RequestBody InvoiceDto invoiceDto) {
        return ResponseEntity.ok(invoiceService.createInvoice(invoiceDto.getOrderId(), invoiceDto));
    }

    @GetMapping("/invoices")
    public ResponseEntity<List<com.uteexpress.entity.Invoice>> getAllInvoices() {
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    @GetMapping("/invoices/status/{status}")
    public ResponseEntity<List<com.uteexpress.entity.Invoice>> getInvoicesByStatus(@PathVariable String status) {
        return ResponseEntity.ok(invoiceService.getInvoicesByStatus(com.uteexpress.entity.InvoiceStatus.valueOf(status)));
    }

    @GetMapping("/invoices/date-range")
    public ResponseEntity<List<com.uteexpress.entity.Invoice>> getInvoicesByDateRange(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        return ResponseEntity.ok(invoiceService.getInvoicesByDateRange(startDate, endDate));
    }

    // Financial Reports
    @PostMapping("/reports/generate")
    public ResponseEntity<com.uteexpress.entity.FinancialReport> generateReport(
            @RequestParam String reportType,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        return ResponseEntity.ok(financialReportService.generateReport(
                com.uteexpress.entity.ReportType.valueOf(reportType), startDate, endDate));
    }

    @GetMapping("/reports")
    public ResponseEntity<List<com.uteexpress.entity.FinancialReport>> getAllReports() {
        return ResponseEntity.ok(financialReportService.getAllReports());
    }

    @GetMapping("/reports/summary")
    public ResponseEntity<FinancialReportDto> getFinancialSummary(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        return ResponseEntity.ok(financialReportService.getFinancialSummary(startDate, endDate));
    }

    // Debt Management
    @PostMapping("/debts")
    public ResponseEntity<com.uteexpress.entity.Debt> createDebt(@RequestBody DebtDto debtDto) {
        return ResponseEntity.ok(debtService.createDebt(debtDto));
    }

    @GetMapping("/debts")
    public ResponseEntity<List<com.uteexpress.entity.Debt>> getAllDebts() {
        return ResponseEntity.ok(debtService.getAllDebts());
    }

    @PostMapping("/debts/{debtId}/payment")
    public ResponseEntity<com.uteexpress.entity.Debt> makePayment(
            @PathVariable Long debtId,
            @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(debtService.makePayment(debtId, amount));
    }
}
