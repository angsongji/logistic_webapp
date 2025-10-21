package com.uteexpress.service.accountant;

import com.uteexpress.dto.accountant.InvoiceDto;
import com.uteexpress.entity.Invoice;
import com.uteexpress.entity.InvoiceStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface InvoiceService {
    Invoice createInvoice(Long orderId, InvoiceDto invoiceDto);
    List<Invoice> getAllInvoices();
    List<Invoice> getInvoicesByStatus(InvoiceStatus status);
    List<Invoice> getInvoicesByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    Invoice updateInvoiceStatus(Long invoiceId, InvoiceStatus status);
    Invoice getInvoiceById(Long invoiceId);
    Double getTotalRevenueByStatus(InvoiceStatus status);
    Double getTotalRevenueByDateRange(LocalDateTime startDate, LocalDateTime endDate);
}
