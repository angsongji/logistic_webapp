package com.uteexpress.mapper;

import com.uteexpress.dto.customer.OrderRequestDto;
import com.uteexpress.dto.customer.OrderResponseDto;
import com.uteexpress.entity.Order;
// Order items not used in this project
import org.springframework.stereotype.Component;

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
                .codAmount(dto.getCodAmount())
                .weight(dto.getWeight())
                .notes(dto.getNotes())
                .imageUrl(dto.getImageUrl())
                .build();

        // Items are not used in this project, skip mapping

        return order;
    }

    public static OrderResponseDto toDto(Order order) {
        return OrderResponseDto.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode())
                .senderName(order.getSenderName())
                .senderPhone(order.getSenderPhone())
                .senderAddress(order.getSenderAddress())
                .recipientName(order.getRecipientName())
                .recipientPhone(order.getRecipientPhone())
                .recipientAddress(order.getRecipientAddress())
                .shipmentFee(order.getShipmentFee())
                .codAmount(order.getCodAmount())
                .weight(order.getWeight())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus() != null ? order.getStatus().name() : null)
                .imageUrl(order.getImageUrl())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .notes(order.getNotes())
                .items(null)
                .serviceType(order.getServiceType() != null ? order.getServiceType().name() : null)
                .customerId(order.getCustomer() != null ? order.getCustomer().getId() : null)
                .shipperId(order.getShipper() != null ? order.getShipper().getId() : null)
                .build();
    }
    
    public static OrderResponseDto toDtoWithInvoiceAndPayment(Order order, com.uteexpress.entity.Invoice invoice, com.uteexpress.entity.Payment payment) {
        return OrderResponseDto.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode())
                .senderName(order.getSenderName())
                .senderPhone(order.getSenderPhone())
                .senderAddress(order.getSenderAddress())
                .recipientName(order.getRecipientName())
                .recipientPhone(order.getRecipientPhone())
                .recipientAddress(order.getRecipientAddress())
                .shipmentFee(order.getShipmentFee())
                .codAmount(order.getCodAmount())
                .weight(order.getWeight())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus() != null ? order.getStatus().name() : null)
                .imageUrl(order.getImageUrl())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .notes(order.getNotes())
                .items(null)
                .serviceType(order.getServiceType() != null ? order.getServiceType().name() : null)
                .customerId(order.getCustomer() != null ? order.getCustomer().getId() : null)
                .shipperId(order.getShipper() != null ? order.getShipper().getId() : null)
                .invoice(InvoiceMapper.toDto(invoice))
                .payment(PaymentMapper.toDto(payment))
                .build();
    }
}