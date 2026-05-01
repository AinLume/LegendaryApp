package org.example.cafecrm.domain.dto.menu;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateMenuItemRequest(
        @NotNull Integer categoryId,
        @NotBlank @Size(max = 50) String name,
        String description,
        @NotNull @Min(0) Long price,
        String photoUrl,
        @NotNull Boolean isAvailable
) {}