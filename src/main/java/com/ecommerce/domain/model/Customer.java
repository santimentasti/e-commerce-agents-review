package com.ecommerce.domain.model;

import com.ecommerce.domain.exception.DomainException;
import java.util.UUID;

public class Customer {

  private final UUID id;
  private String name;
  private Email email;

  public Customer(UUID id, String name, Email email) {
    if (id == null) {
      throw new DomainException("Customer id cannot be null");
    }
    if (name == null || name.isBlank()) {
      throw new DomainException("Customer name cannot be blank");
    }
    if (email == null) {
      throw new DomainException("Customer email cannot be null");
    }
    this.id = id;
    this.name = name;
    this.email = email;
  }

  public static Customer create(String name, Email email) {
    return new Customer(UUID.randomUUID(), name, email);
  }

  public void updateEmail(Email newEmail) {
    if (newEmail == null) {
      throw new DomainException("New email cannot be null");
    }
    this.email = newEmail;
  }

  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Email getEmail() {
    return email;
  }
}
