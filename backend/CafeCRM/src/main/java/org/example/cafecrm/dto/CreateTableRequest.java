package org.example.cafecrm.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateTableRequest(
    @NotNull Integer number,
    @PositiveOrZero Integer capacity,
    @NotNull Integer posX,
    @NotNull Integer posY
) {}
