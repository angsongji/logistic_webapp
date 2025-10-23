package com.uteexpress.service.customer;

import com.uteexpress.dto.customer.OrderRequestDto;
import com.uteexpress.dto.customer.OrderResponseDto;
import com.uteexpress.entity.User;

import java.util.List;

public interface OrderService {
    OrderResponseDto createOrder(User customer, OrderRequestDto dto) throws Exception;
    OrderResponseDto getByOrderCode(String code);
    List<OrderResponseDto> getOrdersByCustomerUsername(String username);
    List<OrderResponseDto> getOrdersByCustomerUsernameAndStatus(String username, String status);
}
