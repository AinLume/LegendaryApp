package org.example.cafecrm.domain.dto.order;

import org.example.cafecrm.domain.dto.menu.MenuItemResponse;
import org.example.cafecrm.domain.values.Destination;
import org.example.cafecrm.domain.values.OrderItemStatus;

public record OrderItemResponse(
        Long orderItemId,
        Long orderId,
        MenuItemResponse menuItem,
        Integer quantity,
        String comment,
        OrderItemStatus status,
        Destination destination
) {}
