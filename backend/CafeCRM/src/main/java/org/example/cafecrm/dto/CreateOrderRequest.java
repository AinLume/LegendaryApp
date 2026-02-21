package org.example.cafecrm.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.example.cafecrm.enums.OrderType;

import java.util.List;

public record CreateOrderRequest(
        @NotNull OrderType type,
        Integer tableId,          // только для DINE_IN
        Long clientId,            // только для DELIVERY
        String deliveryAddress,   // только для DELIVERY
        @NotEmpty List<OrderItemRequest> items
) {}