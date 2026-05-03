package org.example.cafecrm.mapper;

import org.example.cafecrm.domain.dto.menu.CreateMenuItemRequest;
import org.example.cafecrm.domain.dto.menu.MenuItemResponse;
import org.example.cafecrm.domain.dto.menu.UpdateMenuItemRequest;
import org.example.cafecrm.domain.entity.MenuItem;
import org.example.cafecrm.service.MenuService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = MenuService.class)
public interface MenuItemMapper {
    MenuItemMapper INSTANCE = Mappers.getMapper(MenuItemMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", source = "categoryId")
    @Mapping(target = "createdAt",  ignore = true)
    MenuItem toEntity(CreateMenuItemRequest dto);

    @Mapping(target = "categoryId", source = "category.id")
    MenuItemResponse toResponse(MenuItem dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", source = "categoryId")
    void updateEntityFromRequest(UpdateMenuItemRequest dto, @MappingTarget MenuItem entity);
}
