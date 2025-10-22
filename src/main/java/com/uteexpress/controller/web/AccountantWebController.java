package com.uteexpress.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class AccountantWebController {

    @GetMapping("/web/accountant")
    public String accountantShell(Model model) {
        model.addAttribute("title", "Khu vực kế toán");
        return "accountant/index";
    }

    @GetMapping("/web/accountant/dashboard")
    public String accountantDashboard(Model model) {
        model.addAttribute("title", "Dashboard Kế toán");
        return "accountant/dashboard";
    }

    @GetMapping("/web/accountant/payments")
    public String paymentListPage(Model model) {
        model.addAttribute("title", "Quản lý thanh toán");
        return "accountant/payments";
    }

    @GetMapping("/web/accountant/invoices")
    public String invoicesPage(Model model) {
        model.addAttribute("title", "Quản lý hóa đơn");
        return "accountant/invoices";
    }

    @GetMapping("/web/accountant/invoices/{id}")
    public String invoiceDetailPage(@PathVariable Long id, Model model) {
        model.addAttribute("invoiceId", id);
        model.addAttribute("title", "Chi tiết hóa đơn");
        return "accountant/invoice-detail";
    }

    @GetMapping("/web/accountant/reports")
    public String reportsPage(Model model) {
        model.addAttribute("title", "Báo cáo tài chính");
        return "accountant/reports";
    }

    @GetMapping("/web/accountant/debts")
    public String debtsPage(Model model) {
        model.addAttribute("title", "Quản lý công nợ");
        return "accountant/debts";
    }

    @GetMapping("/web/accountant/commissions")
    public String commissionsPage(Model model) {
        model.addAttribute("title", "Quản lý hoa hồng");
        return "accountant/commissions";
    }

    @GetMapping("/web/accountant/reconciliation")
    public String reconciliationPage(Model model) {
        model.addAttribute("title", "Đối soát ngân hàng");
        return "accountant/reconciliation";
    }
}
