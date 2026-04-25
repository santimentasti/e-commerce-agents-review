package com.ecommerce.api.dto;

import java.util.UUID;

public record CustomerResponse(
    UUID id,
    String name,
    String email
) {
}
