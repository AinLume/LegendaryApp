package org.example.cafecrm.dto;

import org.example.cafecrm.enums.MenuItemType;

public record MenuItemResponse(
        Long menuItemId,
        MenuCategoryResponse category,
        String name,
        String description,
        Long price,
        String photoUrl,
        MenuItemType type,
        Boolean isAvailable
) {}
