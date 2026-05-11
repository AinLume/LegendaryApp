package org.example.cafecrm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Menu", description = "Управление меню ресторана: категории и позиции (блюда/напитки)")
@SecurityRequirement(name = "bearerAuth")
public class MenuController {

    private final MenuService menuService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Получить все категории меню с позициями",
            description = "Возвращает список всех категорий меню со вложенными позициями (items). " +
                    "Доступно любому аутентифицированному пользователю."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Успешно",
            content = @Content(schema = @Schema(implementation = MenuCategoryDetailResponse.class))
    )
    @ApiResponse(responseCode = "401", description = "Требуется аутентификация")
    public ResponseEntity<@NotNull List<MenuCategoryDetailResponse>> getAllCategoriesWithItems() {

        log.info("MenuController:getAllCategoriesWithItems.start");

        List<MenuCategoryDetailResponse> response = menuService.getAllMenuCategoriesWithItems();

        log.info("MenuController:getAllCategoriesWithItems.end, count: {}", response.size());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/items")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Получить позиции по ID категории",
            description = "Возвращает категорию меню со списком всех её позиций. " +
                    "Доступно любому аутентифицированному пользователю."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешно",
                    content = @Content(schema = @Schema(implementation = MenuCategoryDetailResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Требуется аутентификация"),
            @ApiResponse(responseCode = "404", description = "Категория с указанным id не найдена")
    })
    public ResponseEntity<@NotNull MenuCategoryDetailResponse> getAllItemsByCategoryId(
            @RequestParam
            @Parameter(description = "ID категории меню", example = "1")
            Integer categoryId
    ) {
        log.info("MenuController:getAllItemsByCategoryId.start, categoryId: {}",  categoryId);

        MenuCategoryDetailResponse response = menuService.getAllMenuItemsByCategoryId(categoryId);

        log.info("MenuController:getAllItemsByCategoryId.end, itemsCount: {}", response.items().size());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/categories")
    @PreAuthorize("hasAnyRole('ADMIN', 'COOK', 'BARTENDER')")
    @Operation(
            summary = "Создать категорию меню",
            description = "Создаёт новую категорию меню. Название категории должно быть уникальным. " +
                    "Доступно администраторам, поварам и барменам."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Категория успешно создана",
                    content = @Content(schema = @Schema(implementation = MenuCategoryResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Некорректные данные в запросе"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён — требуется роль ADMIN, COOK или BARTENDER"),
            @ApiResponse(responseCode = "409", description = "Категория с таким названием уже существует")
    })
    public ResponseEntity<@NotNull MenuCategoryResponse> createCategory(
            @RequestBody @Valid
            @Parameter(description = "Данные для создания категории", required = true)
            CreateMenuCategoryRequest request
    ) {
        log.info("MenuController:createCategory.start, dto: {}", request);

        MenuCategoryResponse response = menuService.createMenuCategory(request);

        log.info("MenuController:createCategory.end, response: {}", response);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/items")
    @PreAuthorize("hasAnyRole('ADMIN', 'COOK', 'BARTENDER')")
    @Operation(
            summary = "Создать позицию меню",
            description = "Создаёт новую позицию (блюдо/напиток) в меню. Проверяет уникальность " +
                    "в рамках категории и типа. Автоматически устанавливает статус доступности в true. " +
                    "Доступно администраторам, поварам и барменам."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Позиция успешно создана",
                    content = @Content(schema = @Schema(implementation = MenuItemResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Некорректные данные в запросе"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён — требуется роль ADMIN, COOK или BARTENDER"),
            @ApiResponse(responseCode = "404", description = "Указанная категория не найдена"),
            @ApiResponse(responseCode = "409", description = "Позиция с таким названием уже существует в категории")
    })
    public ResponseEntity<@NotNull MenuItemResponse> createItem(
            @RequestBody @Valid
            @Parameter(description = "Данные для создания позиции меню", required = true)
            CreateMenuItemRequest request) {

        log.info("MenuController:createItem.start, dto: {}", request);

        MenuItemResponse response = menuService.createMenuItem(request);

        log.info("MenuController:createItem.end, response: {}", response);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/items/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COOK', 'BARTENDER')")
    @Operation(
            summary = "Обновить позицию меню",
            description = "Обновляет существующую позицию меню. При смене категории проверяет её существование. " +
                    "Доступно администраторам, поварам и барменам."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Позиция успешно обновлена",
                    content = @Content(schema = @Schema(implementation = MenuItemResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Некорректные данные в запросе"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён — требуется роль ADMIN, COOK или BARTENDER"),
            @ApiResponse(responseCode = "404", description = "Позиция меню или категория не найдены")
    })
    public ResponseEntity<@NotNull MenuItemResponse> updateItem(
            @PathVariable
            @Parameter(description = "ID позиции меню", example = "1")
            Long id,

            @RequestBody @Valid
            @Parameter(description = "Данные для обновления позиции", required = true)
            UpdateMenuItemRequest request
    ) {
        log.info("MenuController:updateItem.start, dto: {}", request);

        MenuItemResponse response = menuService.updateMenuItem(id, request);

        log.info("MenuController:updateItem.end, response: {}", response);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/categories/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COOK', 'BARTENDER')")
    @Operation(
            summary = "Удалить категорию меню",
            description = "Удаляет категорию меню по идентификатору. Каскадное удаление позиций " +
                    "определяется на уровне JPA-сущности. Доступно администраторам, поварам и барменам."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Категория успешно удалена"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён — требуется роль ADMIN, COOK или BARTENDER"),
            @ApiResponse(responseCode = "404", description = "Категория с указанным id не найдена")
    })
    public ResponseEntity<@NotNull Void> deleteCategory(
            @PathVariable
            @Parameter(description = "ID категории меню", example = "1")
            Integer id) {

        log.info("MenuController:deleteCategory.start, categoryId: {}",  id);

        menuService.deleteMenuCategoryById(id);

        log.info("MenuController:deleteCategory.end");

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/items/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COOK', 'BARTENDER')")
    @Operation(
            summary = "Удалить позицию меню",
            description = "Удаляет позицию меню по идентификатору. Доступно администраторам, поварам и барменам."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Позиция успешно удалена"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён — требуется роль ADMIN, COOK или BARTENDER"),
            @ApiResponse(responseCode = "404", description = "Позиция с указанным id не найдена")
    })
    public ResponseEntity<@NotNull Void> deleteItem(
            @PathVariable
            @Parameter(description = "ID позиции меню", example = "1")
            Long id) {

        log.info("MenuController:deleteItem.start, itemId: {}",  id);

        menuService.deleteMenuItemById(id);

        log.info("MenuController:deleteItem.end");

        return ResponseEntity.noContent().build();
    }
}