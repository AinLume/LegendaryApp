package org.example.cafecrm.dto;

import jakarta.validation.constraints.NotNull;
import org.example.cafecrm.enums.PaymentMethod;

public record CloseOrderRequest(
        @NotNull PaymentMethod paymentMethod
) {}
