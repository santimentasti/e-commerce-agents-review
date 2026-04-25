package com.ecommerce.infrastructure.persistence;

import com.ecommerce.application.port.output.CustomerRepository;
import com.ecommerce.domain.model.Customer;
import com.ecommerce.domain.model.Email;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class CustomerRepositoryAdapter implements CustomerRepository {

  private final SpringCustomerRepository springRepository;

  public CustomerRepositoryAdapter(SpringCustomerRepository springRepository) {
    this.springRepository = springRepository;
  }

  @Override
  public Customer save(Customer customer) {
    CustomerJpaEntity entity = toEntity(customer);
    CustomerJpaEntity saved = springRepository.save(entity);
    return toDomain(saved);
  }

  @Override
  public Optional<Customer> findById(UUID id) {
    return springRepository.findById(id).map(this::toDomain);
  }

  @Override
  public List<Customer> findAll() {
    return springRepository.findAll().stream()
        .map(this::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public boolean existsByEmail(String email) {
    return springRepository.existsByEmail(email);
  }

  private CustomerJpaEntity toEntity(Customer customer) {
    return new CustomerJpaEntity(
        customer.getId(),
        customer.getName(),
        customer.getEmail().value());
  }

  private Customer toDomain(CustomerJpaEntity entity) {
    return new Customer(entity.getId(), entity.getName(), new Email(entity.getEmail()));
  }
}
