package org.example.cafecrm.dto;

import org.example.cafecrm.enums.OrderStatus;
import org.example.cafecrm.enums.OrderType;
import org.example.cafecrm.enums.PaymentMethod;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long orderId,
        OrderType type,
        TableShortResponse table,
        Long clientId,
        OrderStatus status,
        Long totalAmount,
        PaymentMethod paymentMethod,
        List<OrderItemResponse> items,
        LocalDateTime createdAt,
        LocalDateTime closedAt
) {}