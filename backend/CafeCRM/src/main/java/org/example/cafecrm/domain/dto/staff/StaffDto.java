package org.example.cafecrm.domain.dto.staff;

import org.example.cafecrm.domain.values.StaffRole;

public record StaffDto(
        Long id,
        String name,
        String email,
        String phone,
        StaffRole role
) {}