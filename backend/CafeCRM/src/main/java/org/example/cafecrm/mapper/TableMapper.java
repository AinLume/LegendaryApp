package org.example.cafecrm.mapper;

import org.example.cafecrm.domain.dto.table.CreateTableRequest;
import org.example.cafecrm.domain.dto.table.TableShortResponse;
import org.example.cafecrm.domain.dto.table.TableResponse;
import org.example.cafecrm.domain.entity.Tables;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TableMapper {
    MenuItemMapper INSTANCE = Mappers.getMapper(MenuItemMapper.class);

    @Mapping(target = "tableId", source = "id")
    TableResponse toResponse(Tables entity);

    @Mapping(target = "tableId", source = "id")
    TableShortResponse toShortResponse(Tables entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "reservations", ignore = true)
    @Mapping(target = "status",  ignore = true)
    Tables toEntity(CreateTableRequest dto);
}
