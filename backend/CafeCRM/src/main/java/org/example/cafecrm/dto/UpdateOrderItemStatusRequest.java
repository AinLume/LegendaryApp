package org.example.cafecrm.dto;

import jakarta.validation.constraints.NotNull;
import org.example.cafecrm.enums.OrderItemStatus;

public record UpdateOrderItemStatusRequest(
        @NotNull OrderItemStatus status
) {}