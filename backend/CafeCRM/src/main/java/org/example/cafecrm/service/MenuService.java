package org.example.cafecrm.service;

import lombok.RequiredArgsConstructor;
import org.example.cafecrm.domain.dto.menu.*;
import org.example.cafecrm.domain.entity.MenuCategory;
import org.example.cafecrm.domain.entity.MenuItem;
import org.example.cafecrm.exception.ConflictException;
import org.example.cafecrm.exception.NotFoundException;
import org.example.cafecrm.mapper.MenuItemMapper;
import org.example.cafecrm.repository.MenuCategoryRepository;
import org.example.cafecrm.repository.MenuItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuCategoryRepository menuCategoryRepository;
    private final MenuItemRepository menuItemRepository;

    private final MenuItemMapper menuItemMapper;

    @Transactional(readOnly = true)
    public MenuCategory getMenuCategoryById(Integer id) {
        return menuCategoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Category with id - %d not found", id)
                ));
    }

    @Transactional(readOnly = true)
    public List<MenuCategoryDetailResponse> getAllMenuCategoriesWithItems() {
        return menuCategoryRepository.findAllCategoriesWithItems()
                .stream()
                .map(menuCategory -> new MenuCategoryDetailResponse(
                        menuCategory.getId(),
                        menuCategory.getName(),
                        menuCategory.getItems()
                                .stream()
                                .map(menuItemMapper::toResponse)
                                .toList()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public MenuCategoryDetailResponse getAllMenuItemsByCategoryId(Integer categoryId) {

        MenuCategory entity = menuCategoryRepository.findAllItemsByCategoryId(categoryId);

        return new MenuCategoryDetailResponse(
                entity.getId(),
                entity.getName(),
                entity.getItems()
                        .stream()
                        .map(menuItemMapper::toResponse)
                        .toList()
        );
    }

    @Transactional
    public MenuCategoryResponse createMenuCategory(CreateMenuCategoryRequest dto) {
        if (menuCategoryRepository.existsByName(dto.name()))
            throw new ConflictException("Category already exists");

        MenuCategory entity = new MenuCategory();
        entity.setName(dto.name());
        MenuCategory saved = menuCategoryRepository.save(entity);

        return new MenuCategoryResponse(saved.getId(), saved.getName());
    }

    @Transactional
    public MenuItemResponse createMenuItem(CreateMenuItemRequest dto) {

        MenuCategory menuCategory = getMenuCategoryById(dto.categoryId());

        if (menuItemRepository.existsByNameAndCategoryAndType(dto.name(), menuCategory, dto.type())) {
            throw new ConflictException("Item already exists");
        }

        MenuItem entity = menuItemMapper.toEntity(dto);
        entity.setCategory(menuCategory);

        return menuItemMapper.toResponse(menuItemRepository.save(entity));
    }

    @Transactional
    public MenuItemResponse updateMenuItem(Long id, UpdateMenuItemRequest dto) {

        MenuCategory menuCategory = getMenuCategoryById(dto.categoryId());

        MenuItem entity = menuItemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format("MenuItem with id - %d not found", id)
                ));

        menuItemMapper.updateEntityFromRequest(dto, entity);
        entity.setCategory(menuCategory);

        return menuItemMapper.toResponse(menuItemRepository.save(entity));
    }

    @Transactional
    public void deleteMenuItemById(Long id) {
        if (!menuItemRepository.existsById(id))
            throw new NotFoundException(String.format("MenuItem with id - %d not found", id));

        menuItemRepository.deleteById(id);
    }

    @Transactional
    public void deleteMenuCategoryById(Integer id) {
        if (!menuCategoryRepository.existsById(id))
            throw new NotFoundException(String.format("Category with id - %d not found", id));

        menuCategoryRepository.deleteById(id);
    }
}
