package com.uteexpress.service.impl;

import com.uteexpress.dto.accountant.InvoiceDto;
import com.uteexpress.entity.Invoice;
import com.uteexpress.repository.InvoiceRepository;
import com.uteexpress.service.accountant.InvoiceService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    public Invoice createInvoice(Long orderId, InvoiceDto invoiceDto) {
        // Implementation will be added later
        return null;
    }

    @Override
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    @Override
    public List<Invoice> getInvoicesByStatus(Invoice.InvoiceStatus status) {
        return invoiceRepository.findByStatusOrderByIssuedDateDesc(status);
    }

    @Override
    public List<Invoice> getInvoicesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return invoiceRepository.findByIssuedDateBetween(startDate, endDate);
    }

    @Override
    public Invoice updateInvoiceStatus(Long invoiceId, Invoice.InvoiceStatus status) {
        Invoice invoice = invoiceRepository.findById(invoiceId).orElse(null);
        if (invoice != null) {
            invoice.setStatus(status);
            return invoiceRepository.save(invoice);
        }
        return null;
    }

    @Override
    public Invoice getInvoiceById(Long invoiceId) {
        return invoiceRepository.findById(invoiceId).orElse(null);
    }

    @Override
    public Double getTotalRevenueByStatus(Invoice.InvoiceStatus status) {
        return invoiceRepository.getTotalAmountByStatus(status);
    }

    @Override
    public Double getTotalRevenueByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return invoiceRepository.getTotalAmountByDateRange(startDate, endDate);
    }
}
