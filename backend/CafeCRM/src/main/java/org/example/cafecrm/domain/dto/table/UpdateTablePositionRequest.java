package org.example.cafecrm.domain.dto.table;

import jakarta.validation.constraints.NotNull;

public record UpdateTablePositionRequest(
    @NotNull Integer posX,
    @NotNull Integer posY
) {}
