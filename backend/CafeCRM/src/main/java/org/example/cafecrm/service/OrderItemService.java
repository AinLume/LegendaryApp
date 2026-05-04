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

@Service
@RequiredArgsConstructor
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;

    private final ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public OrderItem getEntityById(Long id) {
        return orderItemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Order item with id %d does not exist", id)));
    }

    @Transactional(readOnly = true)
    public OrderItemResponse getById(Long id) {
        return orderItemMapper.toResponse(getEntityById(id));
    }

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

    @Transactional
    public OrderItemResponse updateStatus(Long id, UpdateOrderItemStatusRequest request) {
        OrderItem item = getEntityById(id);

        validateStatusTransition(item.getStatus(), request.status());

        item.setStatus(request.status());
        OrderItem updated = orderItemRepository.save(item);

        eventPublisher.publishEvent(new OrderItemStatusChangedEvent(item.getOrder().getId()));

        return orderItemMapper.toResponse(updated);
    }

    private void validateStatusTransition(OrderItemStatus current, OrderItemStatus next) {
        if (current == OrderItemStatus.READY) {
            throw new ConflictException("Cannot change status of ready item");
        }
        if (current == OrderItemStatus.NEW && next == OrderItemStatus.READY) {
            throw new ConflictException("Item must be in progress before ready");
        }
    }
}