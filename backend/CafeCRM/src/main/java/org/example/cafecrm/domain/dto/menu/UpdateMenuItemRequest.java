package org.example.cafecrm.domain.dto.menu;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.example.cafecrm.domain.values.MenuItemType;

public record UpdateMenuItemRequest(
        Integer categoryId,
        @NotBlank @Size(max = 50) String name,
        String description,
        @Min(0) Long price,
        String photoUrl,
        Boolean isAvailable,
        MenuItemType type
) {}