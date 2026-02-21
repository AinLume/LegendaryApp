package org.example.cafecrm.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.example.cafecrm.enums.MenuItemType;

public record CreateMenuItemRequest(
        @NotNull Integer categoryId,
        @NotBlank @Size(max = 50) String name,
        String description,
        @NotNull @Min(0) Long price,
        String photoUrl,
        @NotNull MenuItemType type
) {}
