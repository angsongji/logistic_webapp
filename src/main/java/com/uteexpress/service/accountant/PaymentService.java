package com.uteexpress.service.accountant;

import com.uteexpress.entity.Payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PaymentService {
    Payment createPayment(Long orderId, BigDecimal amount, String method);
    List<Payment> getAllPayments();
    List<Payment> getPaymentsByMethod(String method);
    List<Payment> getPaymentsByStatus(String status);
    List<Payment> getPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    Payment confirmPayment(Long id);
    Payment refund(Long id);
}
