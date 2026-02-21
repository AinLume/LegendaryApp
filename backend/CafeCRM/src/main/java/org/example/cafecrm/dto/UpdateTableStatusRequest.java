package org.example.cafecrm.dto;

import jakarta.validation.constraints.NotNull;
import org.example.cafecrm.enums.TableStatus;

public record UpdateTableStatusRequest(
    @NotNull TableStatus status
) {}
