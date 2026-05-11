package org.example.cafecrm.domain.dto.order;

import jakarta.validation.constraints.NotNull;
import org.example.cafecrm.domain.values.PaymentMethod;

public record CloseOrderRequest(
        @NotNull PaymentMethod paymentMethod
) {}
