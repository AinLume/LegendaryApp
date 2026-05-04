package org.example.cafecrm.domain.dto.staff;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import org.example.cafecrm.domain.values.StaffRole;

public record StaffUpdateRequest(

        @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
        String name,

        @Email(message = "Email should be valid")
        @Size(max = 100, message = "Email must not exceed 100 characters")
        String email,

        @Size(max = 20, message = "Phone must not exceed 20 characters")
        String phone,

        @Size(min = 6, message = "Password must be at least 6 characters")
        String password,

        StaffRole role

) {}