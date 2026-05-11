package org.example.cafecrm.service;

import lombok.RequiredArgsConstructor;
import org.example.cafecrm.domain.dto.event.OrderItemStatusChangedEvent;
import org.example.cafecrm.domain.dto.order.OrderItemResponse;
import org.example.cafecrm.domain.dto.order.UpdateOrderItemStatusRequest;
import org.example.cafecrm.domain.entity.OrderItem;
import org.example.cafecrm.domain.values.Destination;
import org.example.cafecrm.domain.values.OrderItemStatus;
import org.example.cafecrm.exception.ConflictException;
import org.example.cafecrm.exception.NotFoundException;
import org.example.cafecrm.mapper.OrderItemMapper;
import org.example.cafecrm.repository.OrderItemRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Сервис управления позициями заказа.
 * <p>
 * Обеспечивает работу кухни и бара с позициями заказа ({@link OrderItem}).
 * Предоставляет фильтрацию по назначению (кухня/бар) и управление статусом
 * приготовления позиций.
 * <p>
 * При изменении статуса позиции публикует событие {@link OrderItemStatusChangedEvent}
 * через {@link ApplicationEventPublisher}, что позволяет {@link OrderService}
 * асинхронно проверить готовность всех позиций и обновить статус заказа.
 * <p>
 * Валидация переходов статусов: NEW → IN_PROGRESS → READY.
 * Обратные переходы и повторные изменения запрещены.
 *
 * @author AinLume
 * @see OrderItem
 * @see OrderService
 * @see ApplicationEventPublisher
 */
@Service
@RequiredArgsConstructor
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;

    private final ApplicationEventPublisher eventPublisher;

    /**
     * Возвращает сущность позиции заказа по идентификатору.
     * <p>
     * Базовый метод, переиспользуемый другими методами сервиса.
     *
     * @param id идентификатор позиции заказа
     * @return найденная сущность {@link OrderItem}
     * @throws NotFoundException если позиция с указанным id не существует
     */
    @Transactional(readOnly = true)
    public OrderItem getEntityById(Long id) {
        return orderItemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Order item with id %d does not exist", id)));
    }

    /**
     * Возвращает позиции заказа для кухни.
     * <p>
     * Фильтрует по назначению {@link Destination#KITCHEN}.
     * Опционально фильтрует по статусу приготовления.
     *
     * @param status опциональный фильтр по статусу (null — все статусы)
     * @return список DTO позиций для кухни;
     *         пустой список, если позиций нет
     */
    @Transactional(readOnly = true)
    public List<OrderItemResponse> getKitchenItems(OrderItemStatus status) {
        if (status != null) {
            return orderItemRepository.findAllByDestinationAndStatus(Destination.KITCHEN, status)
                    .stream()
                    .map(orderItemMapper::toResponse)
                    .toList();
        }
        return orderItemRepository.findAllByDestination(Destination.KITCHEN)
                .stream()
                .map(orderItemMapper::toResponse)
                .toList();
    }

    /**
     * Возвращает позиции заказа для бара.
     * <p>
     * Фильтрует по назначению {@link Destination#BAR}.
     * Опционально фильтрует по статусу приготовления.
     *
     * @param status опциональный фильтр по статусу (null — все статусы)
     * @return список DTO позиций для бара;
     *         пустой список, если позиций нет
     */
    @Transactional(readOnly = true)
    public List<OrderItemResponse> getBarItems(OrderItemStatus status) {
        if (status != null) {
            return orderItemRepository.findAllByDestinationAndStatus(Destination.BAR, status).stream()
                    .map(orderItemMapper::toResponse)
                    .toList();
        }
        return orderItemRepository.findAllByDestination(Destination.BAR).stream()
                .map(orderItemMapper::toResponse)
                .toList();
    }

    /**
     * Обновляет статус позиции заказа.
     * <p>
     * Валидирует допустимость перехода статуса перед изменением.
     * После сохранения публикует событие {@link OrderItemStatusChangedEvent}
     * для асинхронного обновления статуса родительского заказа.
     * <p>
     * Допустимые переходы: NEW → IN_PROGRESS → READY.
     * Из READY изменение запрещено. Пропуск IN_PROGRESS запрещён.
     *
     * @param id      идентификатор позиции
     * @param request новый статус
     * @return DTO обновлённой позиции
     * @throws NotFoundException если позиция не найдена
     * @throws ConflictException если переход статуса недопустим
     */
    @Transactional
    public OrderItemResponse updateStatus(Long id, UpdateOrderItemStatusRequest request) {
        OrderItem item = getEntityById(id);

        validateStatusTransition(item.getStatus(), request.status());

        item.setStatus(request.status());
        OrderItem updated = orderItemRepository.save(item);

        eventPublisher.publishEvent(new OrderItemStatusChangedEvent(item.getOrder().getId()));

        return orderItemMapper.toResponse(updated);
    }

    @Transactional
    public List<OrderItem> saveOrderItems(List<OrderItem> entities) {
        return orderItemRepository.saveAll(entities);
    }

    /**
     * Валидирует допустимость перехода статуса позиции.
     * <p>
     * Конечный автомат статусов:
     * <ul>
     *   <li>NEW → IN_PROGRESS — допустимо</li>
     *   <li>IN_PROGRESS → READY — допустимо</li>
     *   <li>NEW → READY — запрещено (требуется IN_PROGRESS)</li>
     *   <li>READY → * — запрещено (позиция готова)</li>
     * </ul>
     *
     * @param current текущий статус позиции
     * @param next    запрашиваемый новый статус
     * @throws ConflictException если переход недопустим
     */
    private void validateStatusTransition(OrderItemStatus current, OrderItemStatus next) {
        if (current == OrderItemStatus.READY) {
            throw new ConflictException("Cannot change status of ready item");
        }
        if (current == OrderItemStatus.NEW && next == OrderItemStatus.READY) {
            throw new ConflictException("Item must be in progress before ready");
        }
    }
}