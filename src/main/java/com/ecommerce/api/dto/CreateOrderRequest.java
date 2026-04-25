package com.ecommerce.api.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import java.util.UUID;

public record CreateOrderRequest(
    @NotNull(message = "Customer ID is required")
    UUID customerId,

    @NotEmpty(message = "Order must have at least one item")
    Map<UUID, Integer> productQuantities
) {
}
