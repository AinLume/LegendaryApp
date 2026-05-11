package org.example.cafecrm.mapper;

import java.util.List;
import org.example.cafecrm.domain.dto.order.OrderItemRequest;
import org.example.cafecrm.domain.dto.order.OrderItemResponse;
import org.example.cafecrm.domain.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = MenuItemMapper.class)
public interface OrderItemMapper {

    @Mapping(target = "orderItemId", source = "id")
    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "destination", source = "destination")
    OrderItemResponse toResponse(OrderItem orderItem);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "NEW")
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "menuItem", ignore = true)
    OrderItem toEntity(OrderItemRequest request);

    @Mapping(target = "orderItemId", source = "id")
    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "destination", source = "destination")
    List<OrderItemResponse> toResponseList(List<OrderItem> orderItems);
}