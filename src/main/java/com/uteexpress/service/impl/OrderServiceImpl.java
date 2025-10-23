package com.uteexpress.service.impl;

import com.uteexpress.dto.customer.OrderRequestDto;
import com.uteexpress.dto.customer.OrderResponseDto;
import com.uteexpress.dto.customer.ShippingFeeRequestDto;
import com.uteexpress.dto.customer.ShippingFeeResponseDto;
import com.uteexpress.entity.Order;
import com.uteexpress.entity.User;
import com.uteexpress.mapper.OrderMapper;
import com.uteexpress.repository.OrderRepository;
import com.uteexpress.repository.UserRepository;
import com.uteexpress.repository.InvoiceRepository;
import com.uteexpress.repository.PaymentRepository;
import com.uteexpress.service.CloudinaryService;
import com.uteexpress.service.ShippingFeeService;
import com.uteexpress.service.customer.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CloudinaryService cloudinaryService;
    private final ShippingFeeService shippingFeeService;
    private final UserRepository userRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;

    public OrderServiceImpl(OrderRepository orderRepository,
                            CloudinaryService cloudinaryService,
                            ShippingFeeService shippingFeeService,
                            UserRepository userRepository,
                            InvoiceRepository invoiceRepository,
                            PaymentRepository paymentRepository) {
        this.orderRepository = orderRepository;
        this.cloudinaryService = cloudinaryService;
        this.shippingFeeService = shippingFeeService;
        this.userRepository = userRepository;
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
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
            // Link to User (store user id in customer_id column)
            order.setCustomer(customer);
        }

        // Calculate shipping fee if service type is provided
        if (dto.getServiceType() != null) {
            order.setServiceType(dto.getServiceType());
            if (dto.getShipmentFee() == null) {
                // Create ShippingFeeRequestDto for fee calculation
                ShippingFeeRequestDto feeRequest = new ShippingFeeRequestDto();
                feeRequest.setDeliveryAddress(dto.getRecipientAddress());
                feeRequest.setServiceType(dto.getServiceType().toString());
                feeRequest.setWeight(dto.getWeight());
                feeRequest.setCodAmount(dto.getCodAmount());
                
                // Create pickup address from sender address
                ShippingFeeRequestDto.AddressDto pickupAddress = new ShippingFeeRequestDto.AddressDto();
                pickupAddress.setAddress(dto.getSenderAddress());
                feeRequest.setPickupAddress(pickupAddress);
                
                // Calculate shipping fee
                ShippingFeeResponseDto feeResponse = shippingFeeService.calculateShippingFee(feeRequest);
                if (feeResponse.getSuccess()) {
                    dto.setShipmentFee(BigDecimal.valueOf(feeResponse.getShippingFee()));
                }
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

        // No order_items table in this project; items are not used.

        return OrderMapper.toDto(saved);
    }

    @Override
    public OrderResponseDto getByOrderCode(String code) {
        Order o = orderRepository.findByOrderCode(code).orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        
        // Find related invoice and payment
        Optional<com.uteexpress.entity.Invoice> invoiceOpt = invoiceRepository.findByOrderId(o.getId());
        Optional<com.uteexpress.entity.Payment> paymentOpt = paymentRepository.findByOrderRefId(o.getId());
        
        com.uteexpress.entity.Invoice invoice = invoiceOpt.orElse(null);
        com.uteexpress.entity.Payment payment = paymentOpt.orElse(null);
        
        return OrderMapper.toDtoWithInvoiceAndPayment(o, invoice, payment);
    }

    @Override
    public List<OrderResponseDto> getOrdersByCustomerUsername(String username) {
        // Prefer querying by the linked customer.id (user id) for robustness
        List<Order> orders = List.of();
        try {
            var userOpt = userRepository.findByUsername(username);
            if (userOpt.isPresent()) {
                Long uid = userOpt.get().getId();
                orders = orderRepository.findByCustomerId(uid);
            }
        } catch (Exception ignored) {
        }

        // Fallback to old behavior if nothing found
        if (orders == null || orders.isEmpty()) {
            orders = orderRepository.findBySenderName(username);
        }
        return orders.stream().map(order -> {
            Optional<com.uteexpress.entity.Invoice> invoiceOpt = invoiceRepository.findByOrderId(order.getId());
            Optional<com.uteexpress.entity.Payment> paymentOpt = paymentRepository.findByOrderRefId(order.getId());
            
            com.uteexpress.entity.Invoice invoice = invoiceOpt.orElse(null);
            com.uteexpress.entity.Payment payment = paymentOpt.orElse(null);
            
            return OrderMapper.toDtoWithInvoiceAndPayment(order, invoice, payment);
        }).collect(Collectors.toList());
    }

    @Override
    public List<OrderResponseDto> getOrdersByCustomerUsernameAndStatus(String username, String status) {
        List<Order> orders = List.of();
        try {
            var userOpt = userRepository.findByUsername(username);
            if (userOpt.isPresent()) {
                Long uid = userOpt.get().getId();
                if (status == null || status.isBlank()) {
                    orders = orderRepository.findByCustomerId(uid);
                } else {
                    try {
                        Order.OrderStatus st = Order.OrderStatus.valueOf(status);
                        orders = orderRepository.findByCustomerIdAndStatus(uid, st);
                    } catch (IllegalArgumentException ex) {
                        // unknown status string: return empty and let fallback handle
                        orders = List.of();
                    }
                }
            }
        } catch (Exception ignored) {
        }

        if (orders == null || orders.isEmpty()) {
            // Fallback: try sender name filtering or status only
            if (status == null || status.isBlank()) {
                orders = orderRepository.findBySenderName(username);
            } else {
                try {
                    Order.OrderStatus st = Order.OrderStatus.valueOf(status);
                    var byStatus = orderRepository.findByStatus(st);
                    orders = byStatus.stream().filter(o -> username.equals(o.getSenderName())).collect(Collectors.toList());
                } catch (IllegalArgumentException ex) {
                    orders = List.of();
                }
            }
        }

        return orders.stream().map(order -> {
            Optional<com.uteexpress.entity.Invoice> invoiceOpt = invoiceRepository.findByOrderId(order.getId());
            Optional<com.uteexpress.entity.Payment> paymentOpt = paymentRepository.findByOrderRefId(order.getId());
            
            com.uteexpress.entity.Invoice invoice = invoiceOpt.orElse(null);
            com.uteexpress.entity.Payment payment = paymentOpt.orElse(null);
            
            return OrderMapper.toDtoWithInvoiceAndPayment(order, invoice, payment);
        }).collect(Collectors.toList());
    }
}
