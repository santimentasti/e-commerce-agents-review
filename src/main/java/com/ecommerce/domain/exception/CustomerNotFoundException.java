package com.ecommerce.domain.exception;

import java.util.UUID;

public class CustomerNotFoundException extends DomainException {

  public CustomerNotFoundException(UUID id) {
    super("Customer not found with id: " + id);
  }
}
