package org.example.cafecrm.service;

import org.example.cafecrm.domain.dto.menu.*;
import org.example.cafecrm.domain.entity.MenuCategory;
import org.example.cafecrm.domain.entity.MenuItem;
import org.example.cafecrm.domain.values.MenuItemType;
import org.example.cafecrm.exception.ConflictException;
import org.example.cafecrm.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.mockito.InjectMocks;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MenuServiceTest extends BaseServiceTest {

    @InjectMocks
    private MenuService menuService;

    @BeforeEach
    void setUp() {
        menuService = new MenuService(menuCategoryRepository, menuItemRepository, menuItemMapper);
        setUpBaseEntities();
    }

    @Test
    void getMenuCategoryById_shouldReturnCategory() {
        when(menuCategoryRepository.findById(1)).thenReturn(Optional.of(testMenuCategory));

        MenuCategory result = menuService.getMenuCategoryById(1);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("Test Category");
        verify(menuCategoryRepository).findById(1);
    }

    @Test
    void getMenuCategoryById_shouldThrowNotFoundException_whenCategoryNotExists() {
        when(menuCategoryRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> menuService.getMenuCategoryById(999))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Category with id - 999 not found");
    }

    @Test
    void getMenuItemById_shouldReturnMenuItem() {
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(testMenuItem));

        MenuItem result = menuService.getMenuItemById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Item");
        verify(menuItemRepository).findById(1L);
    }

    @Test
    void getMenuItemById_shouldThrowNotFoundException_whenMenuItemNotExists() {
        when(menuItemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> menuService.getMenuItemById(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Menu item with id - 999 not found");
    }

    @Test
    void getAllMenuCategoriesWithItems_shouldReturnAllCategoriesWithItems() {
        MenuCategory category2 = createTestMenuCategory(2, "Category 2");
        category2.setItems(Arrays.asList(testMenuItem));

        List<MenuCategory> categories = Arrays.asList(testMenuCategory, category2);
        when(menuCategoryRepository.findAllCategoriesWithItems()).thenReturn(categories);

        MenuItemResponse itemResponse = new MenuItemResponse(
                1L, 1, "Test Item", "Test description", 1000L,
                "http://example.com/photo.jpg", MenuItemType.FOOD, true
        );
        when(menuItemMapper.toResponse(testMenuItem)).thenReturn(itemResponse);

        List<MenuCategoryDetailResponse> result = menuService.getAllMenuCategoriesWithItems();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).menuCategoryId()).isEqualTo(1);
        assertThat(result.get(1).menuCategoryId()).isEqualTo(2);

        verify(menuCategoryRepository).findAllCategoriesWithItems();
        verify(menuItemMapper).toResponse(any(MenuItem.class));
    }

    @Test
    void getAllMenuCategoriesWithItems_shouldReturnEmptyList_whenNoCategories() {
        when(menuCategoryRepository.findAllCategoriesWithItems()).thenReturn(List.of());

        List<MenuCategoryDetailResponse> result = menuService.getAllMenuCategoriesWithItems();

        assertThat(result).isEmpty();
        verify(menuCategoryRepository).findAllCategoriesWithItems();
    }

    @Test
    void getAllMenuItemsByCategoryId_shouldReturnCategoryWithItems() {
        testMenuCategory.setItems(Arrays.asList(testMenuItem));
        when(menuCategoryRepository.findAllItemsByCategoryId(1)).thenReturn(testMenuCategory);

        MenuItemResponse itemResponse = new MenuItemResponse(
                1L, 1, "Test Item", "Test description", 1000L,
                "http://example.com/photo.jpg", MenuItemType.FOOD, true
        );
        when(menuItemMapper.toResponse(testMenuItem)).thenReturn(itemResponse);

        MenuCategoryDetailResponse result = menuService.getAllMenuItemsByCategoryId(1);

        assertThat(result).isNotNull();
        assertThat(result.menuCategoryId()).isEqualTo(1);
        assertThat(result.name()).isEqualTo("Test Category");
        assertThat(result.items()).hasSize(1);

        verify(menuCategoryRepository).findAllItemsByCategoryId(1);
    }

    @Test
    void createMenuCategory_shouldCreateCategory() {
        CreateMenuCategoryRequest request = new CreateMenuCategoryRequest("New Category");

        when(menuCategoryRepository.existsByName("New Category")).thenReturn(false);

        MenuCategory savedCategory = createTestMenuCategory(100, "New Category");
        when(menuCategoryRepository.save(any(MenuCategory.class))).thenReturn(savedCategory);

        MenuCategoryResponse result = menuService.createMenuCategory(request);

        assertThat(result.menuCategoryId()).isEqualTo(100);
        assertThat(result.name()).isEqualTo("New Category");

        verify(menuCategoryRepository).existsByName("New Category");
        verify(menuCategoryRepository).save(any(MenuCategory.class));
    }

    @Test
    void createMenuCategory_shouldThrowConflictException_whenCategoryExists() {
        CreateMenuCategoryRequest request = new CreateMenuCategoryRequest("Existing Category");

        when(menuCategoryRepository.existsByName("Existing Category")).thenReturn(true);

        assertThatThrownBy(() -> menuService.createMenuCategory(request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Category already exists");

        verify(menuCategoryRepository).existsByName("Existing Category");
        verify(menuCategoryRepository, never()).save(any(MenuCategory.class));
    }

    @Test
    void createMenuItem_shouldCreateMenuItem() {
        CreateMenuItemRequest request = new CreateMenuItemRequest(
                1, "New Item", "New description", 1500L,
                "http://example.com/new.jpg", MenuItemType.DRINK
        );

        when(menuCategoryRepository.findById(1)).thenReturn(Optional.of(testMenuCategory));
        when(menuItemRepository.existsByNameAndCategoryAndType("New Item", testMenuCategory, MenuItemType.DRINK))
                .thenReturn(false);

        MenuItem entityToSave = createTestMenuItem(null, "New Item", 1500L, MenuItemType.DRINK, testMenuCategory);
        when(menuItemMapper.toEntity(request)).thenReturn(entityToSave);

        MenuItem savedItem = createTestMenuItem(100L, "New Item", 1500L, MenuItemType.DRINK, testMenuCategory);
        savedItem.setIsAvailable(true);
        when(menuItemRepository.save(any(MenuItem.class))).thenReturn(savedItem);

        MenuItemResponse response = new MenuItemResponse(
                100L, 1, "New Item", "New description", 1500L,
                "http://example.com/new.jpg", MenuItemType.DRINK, true
        );
        when(menuItemMapper.toResponse(savedItem)).thenReturn(response);

        MenuItemResponse result = menuService.createMenuItem(request);

        assertThat(result.menuItemId()).isEqualTo(100L);
        assertThat(result.name()).isEqualTo("New Item");
        assertThat(result.isAvailable()).isTrue();

        verify(menuCategoryRepository).findById(1);
        verify(menuItemRepository).existsByNameAndCategoryAndType("New Item", testMenuCategory, MenuItemType.DRINK);
        verify(menuItemMapper).toEntity(request);
        verify(menuItemRepository).save(any(MenuItem.class));
    }

    @Test
    void createMenuItem_shouldThrowConflictException_whenItemExists() {
        CreateMenuItemRequest request = new CreateMenuItemRequest(
                1, "Existing Item", "Description", 1500L,
                "http://example.com/photo.jpg", MenuItemType.FOOD
        );

        when(menuCategoryRepository.findById(1)).thenReturn(Optional.of(testMenuCategory));
        when(menuItemRepository.existsByNameAndCategoryAndType("Existing Item", testMenuCategory, MenuItemType.FOOD))
                .thenReturn(true);

        assertThatThrownBy(() -> menuService.createMenuItem(request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Item already exists");

        verify(menuCategoryRepository).findById(1);
        verify(menuItemRepository).existsByNameAndCategoryAndType("Existing Item", testMenuCategory, MenuItemType.FOOD);
        verify(menuItemRepository, never()).save(any(MenuItem.class));
    }

    @Test
    void updateMenuItem_shouldUpdateMenuItem() {
        UpdateMenuItemRequest request = new UpdateMenuItemRequest(
                1, "Updated Item", "Updated description", 2000L,
                "http://example.com/updated.jpg", false, MenuItemType.DRINK
        );

        when(menuCategoryRepository.findById(1)).thenReturn(Optional.of(testMenuCategory));
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(testMenuItem));

        MenuItem updatedItem = createTestMenuItem(1L, "Updated Item", 2000L, MenuItemType.DRINK, testMenuCategory);
        updatedItem.setIsAvailable(false);
        when(menuItemRepository.save(any(MenuItem.class))).thenReturn(updatedItem);

        MenuItemResponse response = new MenuItemResponse(
                1L, 1, "Updated Item", "Updated description", 2000L,
                "http://example.com/updated.jpg", MenuItemType.DRINK, false
        );
        when(menuItemMapper.toResponse(updatedItem)).thenReturn(response);

        MenuItemResponse result = menuService.updateMenuItem(1L, request);

        assertThat(result.name()).isEqualTo("Updated Item");
        assertThat(result.price()).isEqualTo(2000L);
        assertThat(result.isAvailable()).isFalse();

        verify(menuCategoryRepository).findById(1);
        verify(menuItemRepository).findById(1L);
        verify(menuItemMapper).updateEntityFromRequest(request, testMenuItem);
        verify(menuItemRepository).save(any(MenuItem.class));
    }

    @Test
    void deleteMenuItemById_shouldDeleteMenuItem() {
        when(menuItemRepository.existsById(1L)).thenReturn(true);
        doNothing().when(menuItemRepository).deleteById(1L);

        menuService.deleteMenuItemById(1L);

        verify(menuItemRepository).existsById(1L);
        verify(menuItemRepository).deleteById(1L);
    }

    @Test
    void deleteMenuItemById_shouldThrowNotFoundException_whenMenuItemNotExists() {
        when(menuItemRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> menuService.deleteMenuItemById(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("MenuItem with id - 999 not found");

        verify(menuItemRepository).existsById(999L);
        verify(menuItemRepository, never()).deleteById(any());
    }

    @Test
    void deleteMenuCategoryById_shouldDeleteCategory() {
        when(menuCategoryRepository.existsById(1)).thenReturn(true);
        doNothing().when(menuCategoryRepository).deleteById(1);

        menuService.deleteMenuCategoryById(1);

        verify(menuCategoryRepository).existsById(1);
        verify(menuCategoryRepository).deleteById(1);
    }

    @Test
    void deleteMenuCategoryById_shouldThrowNotFoundException_whenCategoryNotExists() {
        when(menuCategoryRepository.existsById(999)).thenReturn(false);

        assertThatThrownBy(() -> menuService.deleteMenuCategoryById(999))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Category with id - 999 not found");

        verify(menuCategoryRepository).existsById(999);
        verify(menuCategoryRepository, never()).deleteById(any());
    }
}
