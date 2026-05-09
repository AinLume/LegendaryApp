package org.example.cafecrm.service;

import lombok.RequiredArgsConstructor;
import org.example.cafecrm.domain.dto.event.OrderItemStatusChangedEvent;
import org.example.cafecrm.domain.dto.order.CloseOrderRequest;
import org.example.cafecrm.domain.dto.order.CreateOrderRequest;
import org.example.cafecrm.domain.dto.order.OrderItemRequest;
import org.example.cafecrm.domain.dto.order.OrderResponse;
import org.example.cafecrm.domain.entity.*;
import org.example.cafecrm.domain.values.Destination;
import org.example.cafecrm.domain.values.MenuItemType;
import org.example.cafecrm.domain.values.OrderItemStatus;
import org.example.cafecrm.domain.values.OrderStatus;
import org.example.cafecrm.domain.values.OrderType;
import org.example.cafecrm.exception.ConflictException;
import org.example.cafecrm.exception.NotFoundException;
import org.example.cafecrm.mapper.OrderItemMapper;
import org.example.cafecrm.mapper.OrderMapper;
import org.example.cafecrm.repository.OrderRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Сервис управления заказами.
 * <p>
 * Обеспечивает полный жизненный цикл заказа ({@link Order}): создание,
 * отслеживание статуса, закрытие и отмену.
 * <p>
 * Поддерживает два типа заказов: {@link OrderType#DINE_IN} (в зале с привязкой
 * к столику) и {@link OrderType#DELIVERY} (доставка с привязкой к клиенту и адресу).
 * <p>
 * При создании заказа автоматически формируются позиции ({@link OrderItem}),
 * рассчитывается общая сумма и устанавливается начальный статус.
 * <p>
 * Слушает события {@link OrderItemStatusChangedEvent} через {@link EventListener}
 * для автоматического перевода заказа в статус READY, когда все позиции готовы.
 * <p>
 * Валидация закрытия: только READY заказы могут быть закрыты.
 * Валидация отмены: нельзя отменить оплаченный или уже отменённый заказ.
 *
 * @author AinLume
 * @see Order
 * @see OrderItem
 * @see OrderItemService
 * @see EventListener
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final TableService tableService;
    private final ClientService clientService;
    private final StaffService staffService;
    private final MenuService menuService;
    private final OrderItemMapper orderItemMapper;

    /**
     * Возвращает сущность заказа по идентификатору.
     * <p>
     * Базовый метод, переиспользуемый другими методами сервиса.
     *
     * @param id идентификатор заказа
     * @return найденная сущность {@link Order}
     * @throws NotFoundException если заказ с указанным id не существует
     */
    @Transactional(readOnly = true)
    public Order getEntityById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Order with id %d does not exist", id)
                ));
    }

    /**
     * Возвращает DTO заказа по идентификатору.
     * <p>
     * Переиспользует {@link #getEntityById(Long)} для получения сущности.
     *
     * @param id идентификатор заказа
     * @return DTO с данными заказа
     * @throws NotFoundException если заказ с указанным id не существует
     */
    @Transactional(readOnly = true)
    public OrderResponse getById(Long id) {
        return orderMapper.toResponse(getEntityById(id));
    }

    /**
     * Возвращает все заказы.
     *
     * @return список DTO всех заказов;
     *         пустой список, если заказов нет
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getAll() {
        return orderRepository.findAll()
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    /**
     * Возвращает заказы по статусу.
     *
     * @param status статус заказа для фильтрации
     * @return список DTO заказов с указанным статусом;
     *         пустой список, если заказов нет
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllByStatus(OrderStatus status) {
        return orderRepository.findAllByStatus(status)
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    /**
     * Возвращает заказы по идентификатору столика.
     * <p>
     * Актуально для заказов типа {@link OrderType#DINE_IN}.
     *
     * @param tableId идентификатор столика
     * @return список DTO заказов, привязанных к столику;
     *         пустой список, если заказов нет
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getByTable(Integer tableId) {
        return orderRepository.findAllByTableId(tableId)
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    /**
     * Возвращает заказы по идентификатору клиента.
     * <p>
     * Актуально для заказов типа {@link OrderType#DELIVERY}.
     *
     * @param clientId идентификатор клиента
     * @return список DTO заказов клиента;
     *         пустой список, если заказов нет
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getByClient(Long clientId) {
        return orderRepository.findAllByClientId(clientId)
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    /**
     * Создаёт новый заказ.
     * <p>
     * Валидирует тип заказа и обязательные поля.
     * Для {@link OrderType#DINE_IN} требуется {@code tableId}.
     * Для {@link OrderType#DELIVERY} требуются {@code clientId} и {@code deliveryAddress}.
     * <p>
     * Формирует позиции заказа, рассчитывает общую сумму и устанавливает
     * начальный статус {@link OrderStatus#NEW}.
     *
     * @param request  данные для создания заказа
     * @param staffId  идентификатор сотрудника, оформляющего заказ
     * @param clientId  идентификатор клиента, оформляющего заказ самостоятельно
     * @return DTO созданного заказа
     * @throws NotFoundException если сотрудник, столик, клиент или позиция меню не найдены
     * @throws ConflictException если не указаны обязательные поля для выбранного типа заказа
     */
    @Transactional
    public OrderResponse create(CreateOrderRequest request, Long staffId, Long clientId) {
        validateOrderType(request);

        Order order = orderMapper.toEntity(request);

        if (staffId != null) {
            Staff staff = staffService.getEntityById(staffId);
            order.setStaff(staff);
        }

        if (request.type() == OrderType.DINE_IN) {
            Tables table = tableService.getTableById(request.tableId());
            order.setTable(table);
        }
        else if (request.type() == OrderType.DELIVERY) {
            Client client = clientService.getEntityById(clientId);
            order.setClient(client);
            order.setDeliveryAddress(request.deliveryAddress());
        }

        List<OrderItem> items = request.items()
                .stream()
                .map(itemRequest -> createOrderItem(itemRequest, order))
                .toList();

        order.setItems(items);

        long totalAmount = items.stream()
                .mapToLong(item -> item.getMenuItem().getPrice() * item.getQuantity())
                .sum();
        order.setTotalAmount(totalAmount);

        return orderMapper.toResponse(orderRepository.save(order));
    }

    /**
     * Закрывает заказ.
     * <p>
     * Переводит статус заказа из {@link OrderStatus#READY} в {@link OrderStatus#PAID}.
     * Фиксирует способ оплаты и время закрытия.
     * <p>
     * Закрытие возможно только для заказов в статусе READY.
     *
     * @param id      идентификатор заказа
     * @param request данные о способе оплаты
     * @return DTO закрытого заказа
     * @throws NotFoundException если заказ не найден
     * @throws ConflictException если заказ не в статусе READY
     */
    @Transactional
    public OrderResponse close(Long id, CloseOrderRequest request) {
        Order order = getEntityById(id);

        if (order.getStatus() != OrderStatus.READY) {
            throw new ConflictException("Only READY orders can be closed");
        }

        order.setStatus(OrderStatus.PAID);
        order.setPaymentMethod(request.paymentMethod());
        order.setClosedAt(LocalDateTime.now());

        return orderMapper.toResponse(orderRepository.save(order));
    }

    /**
     * Отменяет заказ.
     * <p>
     * Переводит статус заказа в {@link OrderStatus#CANCELLED} и фиксирует
     * время отмены.
     * <p>
     * Отмена невозможна для уже оплаченных ({@link OrderStatus#PAID})
     * или ранее отменённых заказов.
     *
     * @param id идентификатор заказа
     * @return DTO отменённого заказа
     * @throws NotFoundException если заказ не найден
     * @throws ConflictException если заказ уже оплачен или отменён
     */
    @Transactional
    public OrderResponse cancel(Long id) {
        Order order = getEntityById(id);

        if (order.getStatus() == OrderStatus.PAID || order.getStatus() == OrderStatus.CANCELLED) {
            throw new ConflictException("Cannot cancel paid or already cancelled order");
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setClosedAt(LocalDateTime.now());

        return orderMapper.toResponse(orderRepository.save(order));
    }

    /**
     * Обрабатывает событие изменения статуса позиции заказа.
     * <p>
     * Проверяет, все ли позиции заказа перешли в статус {@link OrderItemStatus#READY}.
     * Если все позиции готовы и заказ находится в статусе
     * {@link OrderStatus#NEW} или {@link OrderStatus#IN_PROGRESS},
     * автоматически переводит заказ в статус {@link OrderStatus#READY}.
     * <p>
     * Вызывается асинхронно при публикации {@link OrderItemStatusChangedEvent}
     * из {@link OrderItemService}.
     *
     * @param event событие изменения статуса позиции
     * @see OrderItemService#updateStatus(Long, org.example.cafecrm.domain.dto.order.UpdateOrderItemStatusRequest)
     * @see EventListener
     */
    @EventListener
    @Transactional
    public void onOrderItemStatusChanged(OrderItemStatusChangedEvent event) {
        Order order = getEntityById(event.orderId());

        boolean allReady = order.getItems().stream()
                .allMatch(item -> item.getStatus() == OrderItemStatus.READY);

        if (allReady && (order.getStatus() == OrderStatus.NEW || order.getStatus() == OrderStatus.IN_PROGRESS)) {
            order.setStatus(OrderStatus.READY);
            orderRepository.save(order);
        }
    }

    /**
     * Валидирует обязательные поля в зависимости от типа заказа.
     * <p>
     * Правила валидации:
     * <ul>
     *   <li>{@link OrderType#DINE_IN} — требуется {@code tableId}</li>
     *   <li>{@link OrderType#DELIVERY} — требуются {@code clientId} и {@code deliveryAddress}</li>
     * </ul>
     *
     * @param request данные для создания заказа
     * @throws ConflictException если не указаны обязательные поля для выбранного типа
     */
    private void validateOrderType(CreateOrderRequest request) {
        if (request.type() == OrderType.DINE_IN && request.tableId() == null) {
            throw new ConflictException("Table id is required for DINE_IN orders");
        }
        if (request.type() == OrderType.DELIVERY && request.clientId() == null) {
            throw new ConflictException("Client id is required for DELIVERY orders");
        }
        if (request.type() == OrderType.DELIVERY && request.deliveryAddress() == null) {
            throw new ConflictException("Delivery address is required for DELIVERY orders");
        }
    }

    /**
     * Создаёт сущность позиции заказа на основе запроса.
     * <p>
     * Привязывает позицию к заказу и заполняет данные позиции меню.
     * Начальный статус позиции устанавливается в {@link OrderItemStatus#NEW}.
     *
     * @param request данные позиции из запроса на создание заказа
     * @param order   родительский заказ
     * @return сформированная сущность {@link OrderItem}
     * @throws NotFoundException если позиция меню не найдена
     */
    private OrderItem createOrderItem(OrderItemRequest request, Order order) {

        OrderItem item = orderItemMapper.toEntity(request);

        item.setOrder(order);
        MenuItem menuItem = menuService.getMenuItemById(request.menuItemId());
        item.setMenuItem(menuItem);

        item.setDestination(menuItem.getType() == MenuItemType.FOOD
                ? Destination.KITCHEN
                : Destination.BAR);

        return item;
    }
}