package org.example.cafecrm.mapper;

import org.example.cafecrm.domain.dto.table.CreateTableRequest;
import org.example.cafecrm.domain.dto.table.TableResponse;
import org.example.cafecrm.domain.entity.Tables;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TableMapper {

    TableResponse toResponse(Tables entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "reservations", ignore = true)
    @Mapping(target = "status",  ignore = true)
    Tables toEntity(CreateTableRequest dto);
}
