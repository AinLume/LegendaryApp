package org.example.cafecrm.domain.dto.menu;

import java.util.List;

public record MenuCategoryDetailResponse(
        Integer menuCategoryId,
        String name,
        List<MenuItemResponse> items
) { }
