package org.example.cafecrm.domain.dto.client;

public record ClientDto(
        Long id,
        String name,
        String phone,
        String email,
        String deliveryAddress
) {}