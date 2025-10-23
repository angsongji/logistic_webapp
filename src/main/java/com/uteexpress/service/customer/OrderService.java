package com.uteexpress.service.customer;

import com.uteexpress.dto.customer.OrderRequestDto;
import com.uteexpress.dto.customer.OrderResponseDto;
import com.uteexpress.entity.User;
import com.uteexpress.entity.Order;

import java.util.List;

public interface OrderService {
    OrderResponseDto createOrder(User customer, OrderRequestDto dto) throws Exception;
    OrderResponseDto getByOrderCode(String code);
    Order getById(Long id);
    List<OrderResponseDto> getOrdersByCustomerUsername(String username);
    List<OrderResponseDto> getOrdersByCustomerUsernameAndStatus(String username, String status);
}
