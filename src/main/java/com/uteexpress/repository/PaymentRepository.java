package com.uteexpress.repository;

import com.uteexpress.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByOrderRefCustomerId(Long customerId);
    List<Payment> findByStatus(String status);
    List<Payment> findByMethod(String method);
    List<Payment> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}
