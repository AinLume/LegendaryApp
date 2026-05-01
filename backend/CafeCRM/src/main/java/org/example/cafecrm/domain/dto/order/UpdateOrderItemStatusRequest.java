package org.example.cafecrm.domain.dto.order;

import jakarta.validation.constraints.NotNull;
import org.example.cafecrm.domain.values.OrderItemStatus;

public record UpdateOrderItemStatusRequest(
        @NotNull OrderItemStatus status
) {}