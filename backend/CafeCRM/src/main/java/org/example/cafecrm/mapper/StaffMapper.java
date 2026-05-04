package org.example.cafecrm.mapper;

import org.example.cafecrm.domain.dto.staff.StaffCreateRequest;
import org.example.cafecrm.domain.dto.staff.StaffDto;
import org.example.cafecrm.domain.dto.staff.StaffUpdateRequest;
import org.example.cafecrm.domain.entity.Staff;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface StaffMapper {

    StaffDto toDto(Staff staff);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "reservations", ignore = true)
    Staff toEntity(StaffCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "reservations", ignore = true)
    void updateEntity(StaffUpdateRequest request, @MappingTarget Staff staff);
}