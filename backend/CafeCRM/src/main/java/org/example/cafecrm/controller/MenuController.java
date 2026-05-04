package org.example.cafecrm.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cafecrm.domain.dto.menu.*;
import org.example.cafecrm.service.MenuService;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<@NotNull List<MenuCategoryDetailResponse>> getAllCategoriesWithItems() {

        log.info("MenuController:getAllCategoriesWithItems.start");

        List<MenuCategoryDetailResponse> response = menuService.getAllMenuCategoriesWithItems();

        log.info("MenuController:getAllCategoriesWithItems.end, count: {}", response.size());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/items")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<@NotNull MenuCategoryDetailResponse> getAllItemsByCategoryId(
            @RequestParam Integer categoryId
    ) {
        log.info("MenuController:getAllItemsByCategoryId.start, categoryId: {}",  categoryId);

        MenuCategoryDetailResponse response = menuService.getAllMenuItemsByCategoryId(categoryId);

        log.info("MenuController:getAllItemsByCategoryId.end, itemsCount: {}", response.items().size());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/categories")
    @PreAuthorize("hasAnyRole('ADMIN', 'COOK', 'BARTENDER')")
    public ResponseEntity<@NotNull MenuCategoryResponse> createCategory(
            @RequestBody @Valid CreateMenuCategoryRequest request
    ) {
        log.info("MenuController:createCategory.start, dto: {}", request);

        MenuCategoryResponse response = menuService.createMenuCategory(request);

        log.info("MenuController:createCategory.end, response: {}", response);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/items")
    @PreAuthorize("hasAnyRole('ADMIN', 'COOK', 'BARTENDER')")
    public ResponseEntity<@NotNull MenuItemResponse> createItem(@RequestBody @Valid CreateMenuItemRequest request) {

        log.info("MenuController:createItem.start, dto: {}", request);

        MenuItemResponse response = menuService.createMenuItem(request);

        log.info("MenuController:createItem.end, response: {}", response);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/items/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COOK', 'BARTENDER')")
    public ResponseEntity<@NotNull MenuItemResponse> updateItem(
            @PathVariable Long id,
            @RequestBody @Valid UpdateMenuItemRequest request
    ) {
        log.info("MenuController:updateItem.start, dto: {}", request);

        MenuItemResponse response = menuService.updateMenuItem(id, request);

        log.info("MenuController:updateItem.end, response: {}", response);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/categories/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COOK', 'BARTENDER')")
    public ResponseEntity<@NotNull Void> deleteCategory(@PathVariable Integer id) {

        log.info("MenuController:deleteCategory.start, categoryId: {}",  id);

        menuService.deleteMenuCategoryById(id);

        log.info("MenuController:deleteCategory.end");

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/items/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COOK', 'BARTENDER')")
    public ResponseEntity<@NotNull Void> deleteItem(@PathVariable Long id) {

        log.info("MenuController:deleteItem.start, itemId: {}",  id);

        menuService.deleteMenuItemById(id);

        log.info("MenuController:deleteItem.end");

        return ResponseEntity.noContent().build();
    }
}
