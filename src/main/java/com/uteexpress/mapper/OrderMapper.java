package com.uteexpress.mapper;

import com.uteexpress.dto.customer.OrderRequestDto;
import com.uteexpress.dto.customer.OrderResponseDto;
import com.uteexpress.dto.customer.OrderItemDto;
import com.uteexpress.entity.Order;
import com.uteexpress.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public static Order toEntity(OrderRequestDto dto) {
        Order order = Order.builder()
                .senderName(dto.getSenderName())
                .senderPhone(dto.getSenderPhone())
                .senderAddress(dto.getSenderAddress())
                .recipientName(dto.getRecipientName())
                .recipientPhone(dto.getRecipientPhone())
                .recipientAddress(dto.getRecipientAddress())
                .serviceType(dto.getServiceType())
                .shipmentFee(dto.getShipmentFee())
                .notes(dto.getNotes())
                .build();

        if (dto.getItems() != null) {
            order.setItems(dto.getItems().stream()
                    .map(itemDto -> OrderItem.builder()
                            .name(itemDto.getName())
                            .quantity(itemDto.getQuantity())
                            .unitPrice(itemDto.getUnitPrice())
                            .order(order)
                            .build())
                    .collect(Collectors.toList()));
        }

        return order;
    }

    public static OrderResponseDto toDto(Order order) {
        return OrderResponseDto.builder()
                .orderCode(order.getOrderCode())
                .senderName(order.getSenderName())
                .recipientName(order.getRecipientName())
                .totalAmount(order.getShipmentFee()) // Simplified for now
                .shipmentFee(order.getShipmentFee())
                .status(order.getStatus() != null ? order.getStatus().name() : null)
                .imageUrl(order.getImageUrl())
                .createdAt(order.getCreatedAt())
                .items(order.getItems() != null ? order.getItems().stream()
                        .map(item -> OrderItemDto.builder()
                                .name(item.getName())
                                .quantity(item.getQuantity())
                                .unitPrice(item.getUnitPrice())
                                .build())
                        .collect(Collectors.toList()) : null)
                .build();
    }
}