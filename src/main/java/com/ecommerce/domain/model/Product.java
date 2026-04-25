package com.ecommerce.domain.model;

import com.ecommerce.domain.exception.DomainException;
import com.ecommerce.domain.exception.InsufficientStockException;
import java.util.UUID;

public class Product {

  private final UUID id;
  private String name;
  private Price price;
  private int stockQuantity;

  public Product(UUID id, String name, Price price, int stockQuantity) {
    if (id == null) {
      throw new DomainException("Product id cannot be null");
    }
    if (name == null || name.isBlank()) {
      throw new DomainException("Product name cannot be blank");
    }
    if (price == null) {
      throw new DomainException("Product price cannot be null");
    }
    if (stockQuantity < 0) {
      throw new DomainException("Stock quantity cannot be negative");
    }
    this.id = id;
    this.name = name;
    this.price = price;
    this.stockQuantity = stockQuantity;
  }

  public static Product create(String name, Price price, int stockQuantity) {
    return new Product(UUID.randomUUID(), name, price, stockQuantity);
  }

  public void decreaseStock(int quantity) {
    if (quantity <= 0) {
      throw new DomainException("Quantity to decrease must be positive");
    }
    if (stockQuantity < quantity) {
      throw new InsufficientStockException(name, quantity, stockQuantity);
    }
    this.stockQuantity -= quantity;
  }

  public void increaseStock(int quantity) {
    if (quantity <= 0) {
      throw new DomainException("Quantity to increase must be positive");
    }
    this.stockQuantity += quantity;
  }

  public void updatePrice(Price newPrice) {
    if (newPrice == null) {
      throw new DomainException("New price cannot be null");
    }
    this.price = newPrice;
  }

  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Price getPrice() {
    return price;
  }

  public int getStockQuantity() {
    return stockQuantity;
  }
}
