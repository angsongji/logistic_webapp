package com.uteexpress.service.accountant;

import com.uteexpress.dto.accountant.InvoiceDto;
import com.uteexpress.entity.Invoice;

import java.time.LocalDateTime;
import java.util.List;

public interface InvoiceService {
    Invoice createInvoice(Long orderId, InvoiceDto invoiceDto);
    List<Invoice> getAllInvoices();
    List<Invoice> getInvoicesByStatus(Invoice.InvoiceStatus status);
    List<Invoice> getInvoicesByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    Invoice updateInvoiceStatus(Long invoiceId, Invoice.InvoiceStatus status);
    Invoice getInvoiceById(Long invoiceId);
    Double getTotalRevenueByStatus(Invoice.InvoiceStatus status);
    Double getTotalRevenueByDateRange(LocalDateTime startDate, LocalDateTime endDate);
}
