package com.uteexpress.service.accountant;

import com.uteexpress.entity.Payment;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentService {
    Payment createPayment(Long orderId, BigDecimal amount, String method);
    List<Payment> getAllPayments();
    Payment confirmPayment(Long id);
    Payment refund(Long id);
}
