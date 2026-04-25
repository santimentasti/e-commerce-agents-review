package com.ecommerce.domain.model;

import com.ecommerce.domain.exception.DomainException;
import java.util.regex.Pattern;

public record Email(String value) {

  private static final Pattern EMAIL_PATTERN =
      Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

  public Email {
    if (value == null || value.isBlank()) {
      throw new DomainException("Email cannot be blank");
    }
    if (!EMAIL_PATTERN.matcher(value).matches()) {
      throw new DomainException("Invalid email format: " + value);
    }
  }

  @Override
  public String toString() {
    return value;
  }
}
