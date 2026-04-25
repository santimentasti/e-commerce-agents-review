package com.ecommerce.domain.model;

import com.ecommerce.domain.exception.DomainException;

public class OrderItem {

  private final Product product;
  private final int quantity;
  private final Price unitPrice;

  public OrderItem(Product product, int quantity) {
    if (product == null) {
      throw new DomainException("OrderItem product cannot be null");
    }
    if (quantity <= 0) {
      throw new DomainException("OrderItem quantity must be positive");
    }
    this.product = product;
    this.quantity = quantity;
    this.unitPrice = product.getPrice();
  }

  public Price subtotal() {
    return unitPrice.multiply(quantity);
  }

  public Product getProduct() {
    return product;
  }

  public int getQuantity() {
    return quantity;
  }

  public Price getUnitPrice() {
    return unitPrice;
  }
}
