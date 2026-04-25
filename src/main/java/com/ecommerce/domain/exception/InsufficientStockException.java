package com.ecommerce.domain.exception;

public class InsufficientStockException extends DomainException {

  public InsufficientStockException(String productName, int requested, int available) {
    super(String.format(
        "Insufficient stock for product '%s': requested %d, available %d",
        productName, requested, available));
  }
}
