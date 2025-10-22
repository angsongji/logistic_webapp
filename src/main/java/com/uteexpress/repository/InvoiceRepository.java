package com.uteexpress.repository;

import com.uteexpress.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByStatusOrderByIssueDateDesc(Invoice.InvoiceStatus status);
    List<Invoice> findByIssueDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT SUM(i.finalAmount) FROM Invoice i WHERE i.status = :status")
    Double getTotalAmountByStatus(Invoice.InvoiceStatus status);
    
    @Query("SELECT SUM(i.finalAmount) FROM Invoice i WHERE i.issueDate BETWEEN :startDate AND :endDate")
    Double getTotalAmountByDateRange(LocalDateTime startDate, LocalDateTime endDate);
}
