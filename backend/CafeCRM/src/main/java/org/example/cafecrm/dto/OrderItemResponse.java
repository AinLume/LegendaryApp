package org.example.cafecrm.dto;

import org.example.cafecrm.enums.Destination;
import org.example.cafecrm.enums.OrderItemStatus;

public record OrderItemResponse(
        Long orderItemId,
        Long orderId,
        MenuItemResponse menuItem,
        Integer quantity,
        String comment,
        OrderItemStatus status,
        Destination destination
) {}
