package org.example.cafecrm.service;

import lombok.RequiredArgsConstructor;
import org.example.cafecrm.domain.dto.event.OrderItemStatusChangedEvent;
import org.example.cafecrm.domain.dto.order.CloseOrderRequest;
import org.example.cafecrm.domain.dto.order.CreateOrderRequest;
import org.example.cafecrm.domain.dto.order.OrderItemRequest;
import org.example.cafecrm.domain.dto.order.OrderResponse;
import org.example.cafecrm.domain.entity.*;
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

    @Transactional(readOnly = true)
    public Order getEntityById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Order with id %d does not exist", id)
                ));
    }

    @Transactional(readOnly = true)
    public OrderResponse getById(Long id) {
        return orderMapper.toResponse(getEntityById(id));
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAll() {
        return orderRepository.findAll()
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllByStatus(OrderStatus status) {
        return orderRepository.findAllByStatus(status)
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getByTable(Integer tableId) {
        return orderRepository.findAllByTableId(tableId)
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getByClient(Long clientId) {
        return orderRepository.findAllByClientId(clientId)
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Transactional
    public OrderResponse create(CreateOrderRequest request, Long staffId) {
        validateOrderType(request);

        Order order = orderMapper.toEntity(request);
        Staff staff = staffService.getEntityById(staffId);
        order.setStaff(staff);

        if (request.type() == OrderType.DINE_IN) {

            Tables table = tableService.getTableById(request.tableId());

            order.setTable(table);
        }
        else if (request.type() == OrderType.DELIVERY) {

            Client client = clientService.getEntityById(request.clientId());

            order.setClient(client);
            order.setDeliveryAddress(request.deliveryAddress());
        }

        List<OrderItem> items = request.items()
                .stream()
                .map(itemRequest -> createOrderItem(itemRequest, order))
                .toList();

        order.setItems(items);

        long totalAmount = items
                .stream()
                .mapToLong(item -> item.getMenuItem().getPrice() * item.getQuantity())
                .sum();
        order.setTotalAmount(totalAmount);

        return orderMapper.toResponse(orderRepository.save(order));
    }

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

    private OrderItem createOrderItem(OrderItemRequest request, Order order) {

        OrderItem item = orderItemMapper.toEntity(request);

        item.setOrder(order);
        item.setMenuItem(menuService.getMenuItemById(request.menuItemId()));

        return item;
    }
}