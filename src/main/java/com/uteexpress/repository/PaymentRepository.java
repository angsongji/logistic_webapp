package com.uteexpress.repository;

import com.uteexpress.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByOrderRefCustomerId(Long customerId);
    List<Payment> findByStatus(String status);
    Optional<Payment> findByOrderRefId(Long orderId);
}
