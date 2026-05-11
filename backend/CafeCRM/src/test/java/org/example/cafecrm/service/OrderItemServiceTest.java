package org.example.cafecrm.service;

import java.util.Collections;
import org.example.cafecrm.domain.dto.order.OrderItemResponse;
import org.example.cafecrm.domain.dto.order.UpdateOrderItemStatusRequest;
import org.example.cafecrm.domain.entity.OrderItem;
import org.example.cafecrm.domain.values.Destination;
import org.example.cafecrm.domain.values.OrderItemStatus;
import org.example.cafecrm.exception.ConflictException;
import org.example.cafecrm.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class OrderItemServiceTest extends BaseServiceTest {

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private OrderItemService orderItemService;

    @BeforeEach
    void setUp() {
        orderItemService = new OrderItemService(orderItemRepository, orderItemMapper, eventPublisher);
        setUpBaseEntities();
    }

    @Test
    void getEntityById_shouldReturnOrderItem() {
        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(testOrderItem));

        OrderItem result = orderItemService.getEntityById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(orderItemRepository).findById(1L);
    }

    @Test
    void getEntityById_shouldThrowNotFoundException_whenOrderItemNotExists() {
        when(orderItemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderItemService.getEntityById(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Order item with id 999 does not exist");
    }

    @Test
    void getKitchenItems_shouldReturnKitchenItems() {
        testOrderItem.setDestination(Destination.KITCHEN);

        when(orderItemRepository.findAllByDestination(Destination.KITCHEN))
                .thenReturn(Collections.singletonList(testOrderItem));

        OrderItemResponse response = new OrderItemResponse(
                1L, 1L, null, 2, null, OrderItemStatus.NEW, Destination.KITCHEN
        );
        when(orderItemMapper.toResponse(testOrderItem)).thenReturn(response);

        List<OrderItemResponse> result = orderItemService.getKitchenItems(null);

        assertThat(result).hasSize(1);
        verify(orderItemRepository).findAllByDestination(Destination.KITCHEN);
    }

    @Test
    void getKitchenItems_shouldReturnKitchenItemsWithStatus() {
        testOrderItem.setDestination(Destination.KITCHEN);
        testOrderItem.setStatus(OrderItemStatus.IN_PROGRESS);

        when(orderItemRepository.findAllByDestinationAndStatus(Destination.KITCHEN, OrderItemStatus.IN_PROGRESS))
                .thenReturn(Collections.singletonList(testOrderItem));

        OrderItemResponse response = new OrderItemResponse(
                1L, 1L, null, 2, null, OrderItemStatus.IN_PROGRESS, Destination.KITCHEN
        );
        when(orderItemMapper.toResponse(testOrderItem)).thenReturn(response);

        List<OrderItemResponse> result = orderItemService.getKitchenItems(OrderItemStatus.IN_PROGRESS);

        assertThat(result).hasSize(1);
        verify(orderItemRepository).findAllByDestinationAndStatus(Destination.KITCHEN, OrderItemStatus.IN_PROGRESS);
    }

    @Test
    void getBarItems_shouldReturnBarItems() {
        testOrderItem.setDestination(Destination.BAR);

        when(orderItemRepository.findAllByDestination(Destination.BAR))
                .thenReturn(Collections.singletonList(testOrderItem));

        OrderItemResponse response = new OrderItemResponse(
                1L, 1L, null, 2, null, OrderItemStatus.NEW, Destination.BAR
        );
        when(orderItemMapper.toResponse(testOrderItem)).thenReturn(response);

        List<OrderItemResponse> result = orderItemService.getBarItems(null);

        assertThat(result).hasSize(1);
        verify(orderItemRepository).findAllByDestination(Destination.BAR);
    }

    @Test
    void getBarItems_shouldReturnBarItemsWithStatus() {
        testOrderItem.setDestination(Destination.BAR);
        testOrderItem.setStatus(OrderItemStatus.READY);

        when(orderItemRepository.findAllByDestinationAndStatus(Destination.BAR, OrderItemStatus.READY))
                .thenReturn(Collections.singletonList(testOrderItem));

        OrderItemResponse response = new OrderItemResponse(
                1L, 1L, null, 2, null, OrderItemStatus.READY, Destination.BAR
        );
        when(orderItemMapper.toResponse(testOrderItem)).thenReturn(response);

        List<OrderItemResponse> result = orderItemService.getBarItems(OrderItemStatus.READY);

        assertThat(result).hasSize(1);
        verify(orderItemRepository).findAllByDestinationAndStatus(Destination.BAR, OrderItemStatus.READY);
    }

    @Test
    void updateStatus_shouldUpdateStatusToInProgress() {
        UpdateOrderItemStatusRequest request = new UpdateOrderItemStatusRequest(OrderItemStatus.IN_PROGRESS);
        testOrderItem.setStatus(OrderItemStatus.NEW);

        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(testOrderItem));
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(testOrderItem);

        OrderItemResponse response = new OrderItemResponse(
                1L, 1L, null, 2, null, OrderItemStatus.IN_PROGRESS, Destination.KITCHEN
        );
        when(orderItemMapper.toResponse(testOrderItem)).thenReturn(response);

        OrderItemResponse result = orderItemService.updateStatus(1L, request);

        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo(OrderItemStatus.IN_PROGRESS);
        assertThat(testOrderItem.getStatus()).isEqualTo(OrderItemStatus.IN_PROGRESS);

        verify(orderItemRepository).save(any(OrderItem.class));
    }

    @Test
    void updateStatus_shouldUpdateStatusToReady() {
        UpdateOrderItemStatusRequest request = new UpdateOrderItemStatusRequest(OrderItemStatus.READY);
        testOrderItem.setStatus(OrderItemStatus.IN_PROGRESS);

        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(testOrderItem));
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(testOrderItem);

        OrderItemResponse response = new OrderItemResponse(
                1L, 1L, null, 2, null, OrderItemStatus.READY, Destination.KITCHEN
        );
        when(orderItemMapper.toResponse(testOrderItem)).thenReturn(response);

        OrderItemResponse result = orderItemService.updateStatus(1L, request);

        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo(OrderItemStatus.READY);
    }

    @Test
    void updateStatus_shouldThrowConflictException_whenChangingFromReady() {
        UpdateOrderItemStatusRequest request = new UpdateOrderItemStatusRequest(OrderItemStatus.IN_PROGRESS);
        testOrderItem.setStatus(OrderItemStatus.READY);

        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(testOrderItem));

        assertThatThrownBy(() -> orderItemService.updateStatus(1L, request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Cannot change status of ready item");

        verify(orderItemRepository, never()).save(any(OrderItem.class));
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void updateStatus_shouldThrowConflictException_whenSkippingInProgress() {
        UpdateOrderItemStatusRequest request = new UpdateOrderItemStatusRequest(OrderItemStatus.READY);
        testOrderItem.setStatus(OrderItemStatus.NEW);

        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(testOrderItem));

        assertThatThrownBy(() -> orderItemService.updateStatus(1L, request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Item must be in progress before ready");

        verify(orderItemRepository, never()).save(any(OrderItem.class));
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void updateStatus_shouldPublishEventWithOrderId() {
        UpdateOrderItemStatusRequest request = new UpdateOrderItemStatusRequest(OrderItemStatus.IN_PROGRESS);
        testOrderItem.setStatus(OrderItemStatus.NEW);

        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(testOrderItem));
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(testOrderItem);

        OrderItemResponse response = new OrderItemResponse(
                1L, 1L, null, 2, null, OrderItemStatus.IN_PROGRESS, Destination.KITCHEN
        );
        when(orderItemMapper.toResponse(testOrderItem)).thenReturn(response);

        orderItemService.updateStatus(1L, request);

        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        assertThat(eventCaptor.getValue()).isNotNull();
    }
}
