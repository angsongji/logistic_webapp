package com.uteexpress.service.impl;

import com.uteexpress.entity.Invoice;
import com.uteexpress.entity.Order;
import com.uteexpress.entity.Payment;
import com.uteexpress.repository.InvoiceRepository;
import com.uteexpress.repository.OrderRepository;
import com.uteexpress.repository.PaymentRepository;
import com.uteexpress.service.accountant.PaymentService;
import com.uteexpress.service.notification.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final InvoiceRepository invoiceRepository;
    private final NotificationService notificationService;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              OrderRepository orderRepository,
                              InvoiceRepository invoiceRepository,
                              NotificationService notificationService) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.invoiceRepository = invoiceRepository;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public Payment createPayment(Long orderId, BigDecimal amount, String method) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Payment p = new Payment();
        p.setOrderRef(order);
        p.setAmount(amount);
        p.setMethod(method);
        p.setStatus("PENDING");
        p.setCreatedAt(LocalDateTime.now());
        paymentRepository.save(p);

        order.setStatus(Order.OrderStatus.CHO_GIAO);
        orderRepository.save(order);

        // Note: Customer entity doesn't have notification method, skipping for now
        // notificationService.sendNotification(order.getCustomer(),
        //         "Thanh toán đơn hàng",
        //         "Đơn hàng #" + order.getId() + " đã được thanh toán " + amount + "đ.");

        return p;
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @Override
    public List<Payment> getPaymentsByMethod(String method) {
        return paymentRepository.findByMethod(method);
    }

    @Override
    public List<Payment> getPaymentsByStatus(String status) {
        return paymentRepository.findByStatus(status);
    }

    @Override
    public List<Payment> getPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return paymentRepository.findByCreatedAtBetween(startDate, endDate);
    }

    @Override
    @Transactional
    public Payment confirmPayment(Long id) {
        Payment p = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        // Update payment status (no validation on order status)
        p.setStatus("COMPLETED");
        p.setUpdatedAt(LocalDateTime.now());
        paymentRepository.save(p);
        
        // Update related invoice status to PAID if order exists
        if (p.getOrderRef() != null) {
            Long orderId = p.getOrderRef().getId();
            invoiceRepository.findByOrderId(orderId).ifPresent(invoice -> {
                invoice.setStatus(Invoice.InvoiceStatus.PAID);
                invoice.setPaymentDate(LocalDateTime.now());
                invoiceRepository.save(invoice);
            });
        }
        
        // Note: Customer entity doesn't have notification method, skipping for now
        // notificationService.sendNotification(
        //         p.getOrderRef().getCustomer(),
        //         "Xác nhận thanh toán",
        //         "Thanh toán #" + p.getId() + " đã được xác nhận.");
        return p;
    }

    @Override
    @Transactional
    public Payment refund(Long id) {
        Payment p = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        // Update payment status
        p.setStatus("REFUNDED");
        p.setUpdatedAt(LocalDateTime.now());
        paymentRepository.save(p);
        
        // Update related invoice status back to CANCELLED
        if (p.getOrderRef() != null) {
            Long orderId = p.getOrderRef().getId();
            invoiceRepository.findByOrderId(orderId).ifPresent(invoice -> {
                invoice.setStatus(Invoice.InvoiceStatus.CANCELLED);
                invoice.setPaymentDate(null); // Clear payment date
                invoiceRepository.save(invoice);
            });
        }
        
        // Note: Customer entity doesn't have notification method, skipping for now
        // notificationService.sendNotification(
        //         p.getOrderRef().getCustomer(),
        //         "Hoàn tiền",
        //         "Thanh toán #" + p.getId() + " đã được hoàn lại.");
        return p;
    }
}
