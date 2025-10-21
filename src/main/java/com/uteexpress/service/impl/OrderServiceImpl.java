package com.uteexpress.service.impl;

import com.uteexpress.dto.customer.OrderRequestDto;
import com.uteexpress.dto.customer.OrderResponseDto;
import com.uteexpress.entity.Order;
import com.uteexpress.entity.OrderItem;
import com.uteexpress.entity.User;
import com.uteexpress.mapper.OrderMapper;
import com.uteexpress.repository.OrderItemRepository;
import com.uteexpress.repository.OrderRepository;
import com.uteexpress.service.CloudinaryService;
import com.uteexpress.service.ShippingFeeService;
import com.uteexpress.service.customer.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository itemRepository;
    private final CloudinaryService cloudinaryService;
    private final ShippingFeeService shippingFeeService;

    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderItemRepository itemRepository,
                            CloudinaryService cloudinaryService,
                            ShippingFeeService shippingFeeService) {
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
        this.cloudinaryService = cloudinaryService;
        this.shippingFeeService = shippingFeeService;
    }

    @Override
    @Transactional
    public OrderResponseDto createOrder(User customer, OrderRequestDto dto) throws Exception {
        // Map DTO -> entity
        Order order = OrderMapper.toEntity(dto);

        // generate order code
        order.setOrderCode(UUID.randomUUID().toString().replace("-", "").substring(0,12).toUpperCase());

        // set sender from customer if available
        if (customer != null) {
            order.setSenderName(customer.getFullName());
            order.setSenderPhone(customer.getPhone());
        }

        // Calculate shipping fee if service type is provided
        if (dto.getServiceType() != null) {
            order.setServiceType(dto.getServiceType());
            if (dto.getShipmentFee() == null) {
                dto.setShipmentFee(shippingFeeService.calculateShippingFee(
                    dto.getSenderAddress(), 
                    dto.getRecipientAddress(), 
                    dto.getServiceType()
                ));
            }
        }

        // persist order (items cascade)
        Order saved = orderRepository.save(order);

        // if image present upload to cloudinary and save url
        if (dto.getImage() != null && !dto.getImage().isEmpty()) {
            String url = cloudinaryService.upload(dto.getImage(), "orders");
            saved.setImageUrl(url);
            saved = orderRepository.save(saved);
        }

        // ensure items are saved (cascade should handle, but persist if needed)
        if (saved.getItems() != null) {
            final Order finalSaved = saved;
            saved.getItems().forEach(i -> i.setOrder(finalSaved));
            itemRepository.saveAll(saved.getItems());
        }

        return OrderMapper.toDto(saved);
    }

    @Override
    public OrderResponseDto getByOrderCode(String code) {
        Order o = orderRepository.findByOrderCode(code).orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        return OrderMapper.toDto(o);
    }

    @Override
    public List<OrderResponseDto> getOrdersByCustomerUsername(String username) {
        // simple approach: find orders by senderName matching username.fullName - for production you should relate Order -> User
        List<Order> orders = orderRepository.findBySenderName(username);
        return orders.stream().map(OrderMapper::toDto).collect(Collectors.toList());
    }
}
