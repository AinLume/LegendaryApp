package org.example.cafecrm.domain.dto.table;

import jakarta.validation.constraints.NotNull;
import org.example.cafecrm.domain.values.TableStatus;

public record UpdateTableStatusRequest(
    @NotNull TableStatus status
) {}
