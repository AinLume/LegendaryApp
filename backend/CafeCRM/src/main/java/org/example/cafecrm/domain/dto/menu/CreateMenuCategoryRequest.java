package org.example.cafecrm.domain.dto.menu;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateMenuCategoryRequest(
        @NotBlank @Size(max = 50) String name
) {}