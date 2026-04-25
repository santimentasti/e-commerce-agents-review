package com.ecommerce.application.port.output;

import com.ecommerce.domain.model.Customer;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {

  Customer save(Customer customer);

  Optional<Customer> findById(UUID id);

  List<Customer> findAll();

  boolean existsByEmail(String email);
}
