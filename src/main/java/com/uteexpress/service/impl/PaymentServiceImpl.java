package com.uteexpress.service.impl;

import com.uteexpress.entity.Order;
import com.uteexpress.entity.OrderStatus;
import com.uteexpress.entity.Payment;
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
    private final NotificationService notificationService;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              OrderRepository orderRepository,
                              NotificationService notificationService) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
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

        order.setStatus(OrderStatus.CHO_GIAO);
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
    public Payment confirmPayment(Long id) {
        Payment p = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        p.setStatus("COMPLETED");
        paymentRepository.save(p);
        // Note: Customer entity doesn't have notification method, skipping for now
        // notificationService.sendNotification(
        //         p.getOrderRef().getCustomer(),
        //         "Xác nhận thanh toán",
        //         "Thanh toán #" + p.getId() + " đã được xác nhận.");
        return p;
    }

    @Override
    public Payment refund(Long id) {
        Payment p = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        p.setStatus("REFUNDED");
        paymentRepository.save(p);
        // Note: Customer entity doesn't have notification method, skipping for now
        // notificationService.sendNotification(
        //         p.getOrderRef().getCustomer(),
        //         "Hoàn tiền",
        //         "Thanh toán #" + p.getId() + " đã được hoàn lại.");
        return p;
    }
}
