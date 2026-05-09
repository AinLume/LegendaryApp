package org.example.cafecrm.mapper;

import org.example.cafecrm.domain.dto.order.CreateOrderRequest;
import org.example.cafecrm.domain.dto.order.OrderResponse;
import org.example.cafecrm.domain.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class, TableMapper.class})
public interface OrderMapper {

    @Mapping(target = "orderId", source = "id")
    @Mapping(target = "tableId", source = "table.id")
    @Mapping(target = "clientId", source = "client.id")
    @Mapping(target = "items", source = "items")
    OrderResponse toResponse(Order order);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "NEW")
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "closedAt", ignore = true)
    @Mapping(target = "table", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "staff", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "paymentMethod", ignore = true)
    Order toEntity(CreateOrderRequest request);
}