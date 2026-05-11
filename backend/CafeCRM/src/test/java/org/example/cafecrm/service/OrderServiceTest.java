package org.example.cafecrm.service;

import org.example.cafecrm.domain.dto.event.OrderItemStatusChangedEvent;
import org.example.cafecrm.domain.dto.order.CloseOrderRequest;
import org.example.cafecrm.domain.dto.order.CreateOrderRequest;
import org.example.cafecrm.domain.dto.order.OrderItemRequest;
import org.example.cafecrm.domain.dto.order.OrderResponse;
import org.example.cafecrm.domain.entity.Order;
import org.example.cafecrm.domain.entity.OrderItem;
import org.example.cafecrm.domain.values.OrderItemStatus;
import org.example.cafecrm.domain.values.OrderStatus;
import org.example.cafecrm.domain.values.OrderType;
import org.example.cafecrm.domain.values.PaymentMethod;
import org.example.cafecrm.exception.ConflictException;
import org.example.cafecrm.exception.NotFoundException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class OrderServiceTest extends BaseServiceTest {

    @Mock
    private TableService tableService;

    @Mock
    private ClientService clientService;

    @Mock
    private StaffService staffService;

    @Mock
    private MenuService menuService;

    @Mock
    private OrderItemService orderItemService;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(
                orderRepository, orderMapper, tableService, clientService,
                staffService, menuService, orderItemMapper, orderItemService
        );
        setUpBaseEntities();
    }

    @Test
    void getEntityById_shouldReturnOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        var result = orderService.getEntityById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(orderRepository).findById(1L);
    }

    @Test
    void getEntityById_shouldThrowNotFoundException_whenOrderNotExists() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getEntityById(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Order with id 999 does not exist");
    }

    @Test
    void getById_shouldReturnOrderResponse() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        OrderResponse response = createMockOrderResponse();
        when(orderMapper.toResponse(testOrder)).thenReturn(response);

        OrderResponse result = orderService.getById(1L);

        assertThat(result).isNotNull();
        assertThat(result.orderId()).isEqualTo(1L);
        verify(orderRepository).findById(1L);
        verify(orderMapper).toResponse(testOrder);
    }

    @Test
    void getAll_shouldReturnPageOfOrders() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<@NotNull Order> orderPage = new PageImpl<>(Arrays.asList(testOrder));
        when(orderRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(orderPage);

        OrderResponse response = createMockOrderResponse();
        when(orderMapper.toResponse(testOrder)).thenReturn(response);

        Page<@NotNull OrderResponse> result = orderService.getAll(null, null, null, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(orderRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getByTable_shouldReturnOrdersForTable() {
        when(orderRepository.findAllByTableId(1)).thenReturn(Arrays.asList(testOrder));

        OrderResponse response = createMockOrderResponse();
        when(orderMapper.toResponse(testOrder)).thenReturn(response);

        List<OrderResponse> result = orderService.getByTable(1);

        assertThat(result).hasSize(1);
        verify(orderRepository).findAllByTableId(1);
    }

    @Test
    void create_shouldCreateDineInOrder() {
        CreateOrderRequest request = new CreateOrderRequest(
                OrderType.DINE_IN,
                1,
                null,
                null,
                Arrays.asList(new OrderItemRequest(1L, 2, null))
        );

        when(tableService.getTableById(1)).thenReturn(testTable);
        when(staffService.getEntityById(1L)).thenReturn(testStaff);
        when(menuService.getMenuItemById(1L)).thenReturn(testMenuItem);
        when(orderMapper.toEntity(request)).thenReturn(testOrder);
        when(orderItemMapper.toEntity(any(OrderItemRequest.class))).thenReturn(testOrderItem);

        org.example.cafecrm.domain.entity.Order savedOrder = createTestOrder(1L, OrderType.DINE_IN, OrderStatus.NEW, testTable, testClient, testStaff);
        savedOrder.setItems(Arrays.asList(testOrderItem));
        savedOrder.setTotalAmount(2000L);
        when(orderRepository.save(any())).thenReturn(savedOrder);

        OrderResponse response = createMockOrderResponse();
        when(orderMapper.toResponse(savedOrder)).thenReturn(response);

        OrderResponse result = orderService.create(request, 1L, null);

        assertThat(result).isNotNull();
        verify(tableService).getTableById(1);
        verify(orderRepository).save(any());
    }

    @Test
    void create_shouldThrowConflictException_whenDineInOrderWithoutTableId() {
        CreateOrderRequest request = new CreateOrderRequest(
                OrderType.DINE_IN,
                null,
                null,
                null,
                Arrays.asList(new OrderItemRequest(1L, 2, null))
        );

        assertThatThrownBy(() -> orderService.create(request, 1L, null))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Table id is required for DINE_IN orders");

        verify(orderRepository, never()).save(any());
    }

    @Test
    void create_shouldThrowConflictException_whenDeliveryOrderWithoutClientId() {
        CreateOrderRequest request = new CreateOrderRequest(
                OrderType.DELIVERY,
                null,
                null,
                "Test Address",
                Arrays.asList(new OrderItemRequest(1L, 2, null))
        );

        assertThatThrownBy(() -> orderService.create(request, null, null))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Client id is required for DELIVERY orders");

        verify(orderRepository, never()).save(any());
    }

    @Test
    void close_shouldCloseOrder() {
        CloseOrderRequest request = new CloseOrderRequest(PaymentMethod.CARD);
        testOrder.setStatus(OrderStatus.READY);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        org.example.cafecrm.domain.entity.Order closedOrder = createTestOrder(1L, OrderType.DINE_IN, OrderStatus.PAID, testTable, testClient, testStaff);
        closedOrder.setPaymentMethod(PaymentMethod.CARD);
        when(orderRepository.save(any())).thenReturn(closedOrder);

        OrderResponse response = createMockOrderResponse();
        when(orderMapper.toResponse(closedOrder)).thenReturn(response);

        OrderResponse result = orderService.close(1L, request);

        assertThat(result).isNotNull();
        verify(orderRepository).save(any());
    }

    @Test
    void close_shouldThrowConflictException_whenOrderNotReady() {
        CloseOrderRequest request = new CloseOrderRequest(PaymentMethod.CARD);
        testOrder.setStatus(OrderStatus.NEW);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        assertThatThrownBy(() -> orderService.close(1L, request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Only READY orders can be closed");

        verify(orderRepository).findById(1L);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void cancel_shouldCancelOrder() {
        testOrder.setStatus(OrderStatus.NEW);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        org.example.cafecrm.domain.entity.Order cancelledOrder = createTestOrder(1L, OrderType.DINE_IN, OrderStatus.CANCELLED, testTable, testClient, testStaff);
        when(orderRepository.save(any())).thenReturn(cancelledOrder);

        OrderResponse response = createMockOrderResponse();
        when(orderMapper.toResponse(cancelledOrder)).thenReturn(response);

        OrderResponse result = orderService.cancel(1L);

        assertThat(result).isNotNull();
        verify(orderRepository).save(any());
    }

    @Test
    void cancel_shouldThrowConflictException_whenOrderPaid() {
        testOrder.setStatus(OrderStatus.PAID);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        assertThatThrownBy(() -> orderService.cancel(1L))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Cannot cancel paid or already cancelled order");

        verify(orderRepository).findById(1L);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void onOrderItemStatusChanged_shouldSetOrderReadyWhenAllItemsReady() {
        OrderItem readyItem1 = createTestOrderItem(1L, 2, testMenuItem, testOrder);
        readyItem1.setStatus(OrderItemStatus.READY);
        OrderItem readyItem2 = createTestOrderItem(2L, 1, testMenuItem, testOrder);
        readyItem2.setStatus(OrderItemStatus.READY);

        testOrder.setStatus(OrderStatus.NEW);
        testOrder.setItems(Arrays.asList(readyItem1, readyItem2));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        orderService.onOrderItemStatusChanged(new OrderItemStatusChangedEvent(1L));

        assertThat(testOrder.getStatus()).isEqualTo(OrderStatus.READY);
        verify(orderRepository).save(testOrder);
    }

    @Test
    void onOrderItemStatusChanged_shouldNotSetOrderReadyWhenNotAllItemsReady() {
        OrderItem readyItem = createTestOrderItem(1L, 2, testMenuItem, testOrder);
        readyItem.setStatus(OrderItemStatus.READY);
        OrderItem newItem = createTestOrderItem(2L, 1, testMenuItem, testOrder);
        newItem.setStatus(OrderItemStatus.NEW);

        testOrder.setStatus(OrderStatus.NEW);
        testOrder.setItems(Arrays.asList(readyItem, newItem));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        orderService.onOrderItemStatusChanged(new OrderItemStatusChangedEvent(1L));

        assertThat(testOrder.getStatus()).isEqualTo(OrderStatus.NEW);
        verify(orderRepository, never()).save(any());
    }

    private OrderResponse createMockOrderResponse() {
        return new OrderResponse(
                1L,
                OrderType.DINE_IN,
                1,
                null,
                1L,
                OrderStatus.NEW,
                2000L,
                PaymentMethod.CASH,
                List.of(),
                LocalDateTime.now(),
                null
        );
    }
}
