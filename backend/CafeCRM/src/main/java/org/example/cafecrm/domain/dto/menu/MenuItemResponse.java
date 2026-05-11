package org.example.cafecrm.domain.dto.menu;

import org.example.cafecrm.domain.values.MenuItemType;

public record MenuItemResponse(
        Long menuItemId,
        Integer categoryId,
        String name,
        String description,
        Long price,
        String photoUrl,
        MenuItemType type,
        Boolean isAvailable
) {}
