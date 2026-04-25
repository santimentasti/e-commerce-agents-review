package com.ecommerce.domain.model;

import com.ecommerce.domain.exception.DomainException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public record Price(BigDecimal amount) {

  public Price {
    if (amount == null) {
      throw new DomainException("Price amount cannot be null");
    }
    if (amount.compareTo(BigDecimal.ZERO) < 0) {
      throw new DomainException("Price cannot be negative: " + amount);
    }
    amount = amount.setScale(2, RoundingMode.HALF_UP);
  }

  public static Price of(BigDecimal amount) {
    return new Price(amount);
  }

  public static Price of(String amount) {
    return new Price(new BigDecimal(amount));
  }

  public Price add(Price other) {
    return new Price(this.amount.add(other.amount));
  }

  public Price multiply(int factor) {
    return new Price(this.amount.multiply(BigDecimal.valueOf(factor)));
  }

  @Override
  public String toString() {
    return amount.toPlainString();
  }
}
