package com.uteexpress.entity;

import com.uteexpress.service.impl.NotificationServiceImplBridge;
import jakarta.persistence.PostUpdate;

public class OrderStatusListener {

    @PostUpdate
    public void afterUpdate(Order order) {
        if (order == null || order.getStatus() == null) return;
        if (order.getStatus() == Order.OrderStatus.HOAN_THANH) {
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
        }
    }
}


