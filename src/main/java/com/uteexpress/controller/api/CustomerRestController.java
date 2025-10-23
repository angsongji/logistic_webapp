package com.uteexpress.controller.api;

import com.uteexpress.dto.customer.OrderRequestDto;
import com.uteexpress.entity.User;
import com.uteexpress.mapper.OrderMapper;
import com.uteexpress.service.customer.CustomerService;
import com.uteexpress.service.customer.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/customer")
public class CustomerRestController {

    private final OrderService orderService;
    private final CustomerService customerService;
    private final OrderMapper orderMapper;

    public CustomerRestController(OrderService orderService,
                                  CustomerService customerService,
                                  OrderMapper orderMapper) {
        this.orderService = orderService;
        this.customerService = customerService;
        this.orderMapper = orderMapper;
    }

    @PostMapping("/orders")
    public ResponseEntity<?> createOrder(
            @ModelAttribute OrderRequestDto dto,
            Principal principal) {
        User user = customerService.getByUsername(principal.getName());
        try {
            var order = orderService.createOrder(user, dto);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating order: " + e.getMessage());
        }
    }

    @GetMapping("/orders")
    public ResponseEntity<?> getMyOrders(Principal principal) {
        var list = orderService.getOrdersByCustomerUsername(principal.getName());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/orders/{code}")
    public ResponseEntity<?> getOrderDetail(@PathVariable String code) {
        return ResponseEntity.ok(orderService.getByOrderCode(code));
    }

    @GetMapping("/orders/{code}/tracking")
    public ResponseEntity<?> getOrderTracking(@PathVariable String code, Principal principal) {
        // For now, reuse order detail which contains status; in future extend with timeline
        var dto = orderService.getByOrderCode(code);
        return ResponseEntity.ok(dto);
    }
}
