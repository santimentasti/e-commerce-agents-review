package com.ecommerce.domain.model;

import com.ecommerce.domain.exception.DomainException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Order {

  public enum Status {
    PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
  }

  private final UUID id;
  private final Customer customer;
  private final List<OrderItem> items;
  private Status status;
  private final Instant createdAt;

  public Order(UUID id, Customer customer) {
    if (id == null) {
      throw new DomainException("Order id cannot be null");
    }
    if (customer == null) {
      throw new DomainException("Order customer cannot be null");
    }
    this.id = id;
    this.customer = customer;
    this.items = new ArrayList<>();
    this.status = Status.PENDING;
    this.createdAt = Instant.now();
  }

  public static Order create(Customer customer) {
    return new Order(UUID.randomUUID(), customer);
  }

  public void addItem(OrderItem item) {
    if (status != Status.PENDING) {
      throw new DomainException("Cannot add items to a non-pending order");
    }
    if (item == null) {
      throw new DomainException("Order item cannot be null");
    }
    items.add(item);
  }

  public void confirm() {
    if (status != Status.PENDING) {
      throw new DomainException("Only pending orders can be confirmed");
    }
    if (items.isEmpty()) {
      throw new DomainException("Cannot confirm an order with no items");
    }
    this.status = Status.CONFIRMED;
  }

  public void ship() {
    if (status != Status.CONFIRMED) {
      throw new DomainException("Only confirmed orders can be shipped");
    }
    this.status = Status.SHIPPED;
  }

  public void deliver() {
    if (status != Status.SHIPPED) {
      throw new DomainException("Only shipped orders can be delivered");
    }
    this.status = Status.DELIVERED;
  }

  public void cancel() {
    if (status == Status.DELIVERED || status == Status.SHIPPED) {
      throw new DomainException("Cannot cancel a shipped or delivered order");
    }
    this.status = Status.CANCELLED;
  }

  public Price totalAmount() {
    return items.stream()
        .map(OrderItem::subtotal)
        .reduce(Price.of("0.00"), Price::add);
  }

  public UUID getId() {
    return id;
  }

  public Customer getCustomer() {
    return customer;
  }

  public List<OrderItem> getItems() {
    return Collections.unmodifiableList(items);
  }

  public Status getStatus() {
    return status;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
