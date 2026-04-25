package com.ecommerce.domain.exception;

import java.util.UUID;

public class OrderNotFoundException extends DomainException {

  public OrderNotFoundException(UUID id) {
    super("Order not found with id: " + id);
  }
}
