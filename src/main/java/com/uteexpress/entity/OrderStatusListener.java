package com.uteexpress.entity;

import com.uteexpress.service.accountant.CommissionService;
import com.uteexpress.service.impl.NotificationServiceImplBridge;
import jakarta.persistence.PostUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class OrderStatusListener {

    private static ApplicationContext applicationContext;

    @Autowired
    public void setApplicationContext(ApplicationContext context) {
        OrderStatusListener.applicationContext = context;
    }

    @PostUpdate
    public void afterUpdate(Order order) {
        if (order == null || order.getStatus() == null) return;
        
        if (order.getStatus() == Order.OrderStatus.HOAN_THANH) {
            // Send notification to customer
            String username = null;
            if (order.getCustomer() != null) {
                // Customer entity stores username directly
                username = order.getCustomer().getUsername();
            }
            NotificationServiceImplBridge.sendToUsername(
                    username,
                    "Đơn hàng đã giao thành công",
                    "Đơn hàng " + (order.getOrderCode() != null ? ("#" + order.getOrderCode()) : ("#" + order.getId())) + " đã được giao đến người nhận. Cảm ơn bạn!",
                    "ORDER_COMPLETED"
            );
            
            // Automatically create commission for shipper
            if (order.getShipper() != null && order.getShipmentFee() != null && applicationContext != null) {
                try {
                    CommissionService commissionService = applicationContext.getBean(CommissionService.class);
                    commissionService.createCommission(
                            order.getShipper().getId(),
                            order.getId(),
                            order.getShipmentFee()
                    );
                } catch (Exception e) {
                    // Log the error but don't fail the order update
                    System.err.println("Failed to create commission for order " + order.getId() + ": " + e.getMessage());
                }
            }
        }
    }
}


