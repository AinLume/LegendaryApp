package org.example.cafecrm.domain.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record ClientLoginRequest(
        @NotBlank(message = "Phone is required")
        String phone,

        @NotBlank(message = "Password is required")
        String password
) {}
