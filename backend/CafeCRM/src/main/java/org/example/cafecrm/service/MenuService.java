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

/**
 * Сервис управления меню ресторана.
 * <p>
 * Обеспечивает управление категориями меню ({@link MenuCategory}) и позициями меню
 * ({@link MenuItem}). Поддерживает иерархическую структуру: категория содержит
 * список блюд/напитков.
 * <p>
 * При создании позиции меню проверяет уникальность в рамках категории и типа.
 * Используется при формировании заказов ({@link OrderService}) для получения
 * цен и проверки доступности позиций.
 * <p>
 * Все операции модификации выполняются в транзакционном контексте.
 *
 * @author AinLume
 * @see MenuCategory
 * @see MenuItem
 * @see OrderService
 */
@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuCategoryRepository menuCategoryRepository;
    private final MenuItemRepository menuItemRepository;

    private final MenuItemMapper menuItemMapper;

    /**
     * Возвращает сущность категории меню по идентификатору.
     * <p>
     * Базовый метод, переиспользуемый другими методами сервиса.
     *
     * @param id идентификатор категории
     * @return найденная сущность {@link MenuCategory}
     * @throws NotFoundException если категория с указанным id не найдена
     */
    @Transactional(readOnly = true)
    public MenuCategory getMenuCategoryById(Integer id) {
        return menuCategoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Category with id - %d not found", id)
                ));
    }

    /**
     * Возвращает сущность позиции меню по идентификатору.
     * <p>
     * Базовый метод, переиспользуемый другими сервисами
     * (например, {@link OrderService} при создании заказа).
     *
     * @param id идентификатор позиции меню
     * @return найденная сущность {@link MenuItem}
     * @throws NotFoundException если позиция меню с указанным id не найдена
     */
    @Transactional(readOnly = true)
    public MenuItem getMenuItemById(Long id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Menu item with id - %d not found", id)
                ));
    }

    /**
     * Возвращает все категории меню с их позициями.
     * <p>
     * Использует кастомный запрос репозитория для получения категорий
     * со списком связанных items (жадная загрузка для оптимизации).
     *
     * @return список категорий с вложенными позициями меню;
     *         пустой список, если категорий нет
     */
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

    /**
     * Возвращает категорию меню с позициями по идентификатору категории.
     * <p>
     * Использует кастомный запрос для получения категории со списком items.
     *
     * @param categoryId идентификатор категории
     * @return категория со списком позиций меню
     * @throws NotFoundException если категория не найдена или не содержит позиций
     */
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

    /**
     * Создаёт новую категорию меню.
     * <p>
     * Проверяет уникальность названия категории перед созданием.
     *
     * @param dto данные для создания категории
     * @return DTO созданной категории с присвоенным id
     * @throws ConflictException если категория с таким названием уже существует
     */
    @Transactional
    public MenuCategoryResponse createMenuCategory(CreateMenuCategoryRequest dto) {
        if (menuCategoryRepository.existsByName(dto.name()))
            throw new ConflictException("Category already exists");

        MenuCategory entity = new MenuCategory();
        entity.setName(dto.name());
        MenuCategory saved = menuCategoryRepository.save(entity);

        return new MenuCategoryResponse(saved.getId(), saved.getName());
    }

    /**
     * Создаёт новую позицию меню.
     * <p>
     * Проверяет уникальность позиции в рамках категории и типа
     * (например, нельзя создать два "Капучино" в категории "Кофе" типа DRINK).
     * Автоматически устанавливает статус доступности в {@code true}.
     *
     * @param dto данные для создания позиции меню
     * @return DTO созданной позиции с присвоенным id
     * @throws NotFoundException если указанная категория не найдена
     * @throws ConflictException если позиция с таким названием уже существует в категории
     */
    @Transactional
    public MenuItemResponse createMenuItem(CreateMenuItemRequest dto) {

        MenuCategory menuCategory = getMenuCategoryById(dto.categoryId());

        if (menuItemRepository.existsByNameAndCategoryAndType(dto.name(), menuCategory, dto.type())) {
            throw new ConflictException("Item already exists");
        }

        MenuItem entity = menuItemMapper.toEntity(dto);
        entity.setCategory(menuCategory);
        entity.setIsAvailable(true);

        return menuItemMapper.toResponse(menuItemRepository.save(entity));
    }

    /**
     * Обновляет существующую позицию меню.
     * <p>
     * При смене категории проверяет существование новой категории.
     * Сохраняет все изменения через маппер.
     *
     * @param id  идентификатор обновляемой позиции
     * @param dto данные для обновления
     * @return DTO обновлённой позиции
     * @throws NotFoundException если позиция или категория не найдены
     */
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

    /**
     * Удаляет позицию меню по идентификатору.
     * <p>
     * Проверяет существование позиции перед удалением.
     *
     * @param id идентификатор удаляемой позиции
     * @throws NotFoundException если позиция с указанным id не найдена
     */
    @Transactional
    public void deleteMenuItemById(Long id) {
        if (!menuItemRepository.existsById(id))
            throw new NotFoundException(String.format("MenuItem with id - %d not found", id));

        menuItemRepository.deleteById(id);
    }

    /**
     * Удаляет категорию меню по идентификатору.
     * <p>
     * Проверяет существование категории перед удалением.
     * Каскадное удаление позиций определяется на уровне JPA-сущности.
     *
     * @param id идентификатор удаляемой категории
     * @throws NotFoundException если категория с указанным id не найдена
     */
    @Transactional
    public void deleteMenuCategoryById(Integer id) {
        if (!menuCategoryRepository.existsById(id))
            throw new NotFoundException(String.format("Category with id - %d not found", id));

        menuCategoryRepository.deleteById(id);
    }
}