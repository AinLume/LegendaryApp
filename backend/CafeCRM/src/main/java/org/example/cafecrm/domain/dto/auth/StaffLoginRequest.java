package org.example.cafecrm.domain.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record StaffLoginRequest(
        @NotBlank(message = "Email is required")
        @jakarta.validation.constraints.Email(message = "Email should be valid")
        String email,

        @NotBlank(message = "Password is required")
        String password
) {}