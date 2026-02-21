package org.example.cafecrm.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateTablePositionRequest(
    @NotNull Integer posX,
    @NotNull Integer posY
) {}
