package org.example.cafecrm.mapper;

import org.example.cafecrm.domain.dto.client.ClientCreateRequest;
import org.example.cafecrm.domain.dto.client.ClientDto;
import org.example.cafecrm.domain.dto.client.ClientUpdateRequest;
import org.example.cafecrm.domain.entity.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    ClientDto toDto(Client client);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "orders", ignore = true)
    Client toEntity(ClientCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "orders", ignore = true)
    void updateEntity(ClientUpdateRequest request, @MappingTarget Client client);
}