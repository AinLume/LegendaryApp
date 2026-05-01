package org.example.cafecrm.domain.dto.order;

import org.example.cafecrm.domain.dto.table.TableShortResponse;
import org.example.cafecrm.domain.values.OrderStatus;
import org.example.cafecrm.domain.values.OrderType;
import org.example.cafecrm.domain.values.PaymentMethod;

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