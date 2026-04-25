package com.ecommerce.api.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
    UUID id,
    UUID customerId,
    String customerName,
    List<OrderItemResponse> items,
    String status,
    BigDecimal totalAmount,
    Instant createdAt
) {
}
