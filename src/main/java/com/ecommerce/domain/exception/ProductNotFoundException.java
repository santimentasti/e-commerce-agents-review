package com.ecommerce.domain.exception;

import java.util.UUID;

public class ProductNotFoundException extends DomainException {

  public ProductNotFoundException(UUID id) {
    super("Product not found with id: " + id);
  }
}
