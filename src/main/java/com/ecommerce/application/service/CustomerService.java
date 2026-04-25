package com.ecommerce.application.service;

import com.ecommerce.application.port.input.CreateCustomerUseCase;
import com.ecommerce.application.port.input.GetCustomerUseCase;
import com.ecommerce.application.port.output.CustomerRepository;
import com.ecommerce.domain.exception.CustomerNotFoundException;
import com.ecommerce.domain.exception.DomainException;
import com.ecommerce.domain.model.Customer;
import com.ecommerce.domain.model.Email;
import java.util.List;
import java.util.UUID;

public class CustomerService implements CreateCustomerUseCase, GetCustomerUseCase {

  private final CustomerRepository customerRepository;

  public CustomerService(CustomerRepository customerRepository) {
    this.customerRepository = customerRepository;
  }

  @Override
  public Customer createCustomer(String name, String email) {
    if (customerRepository.existsByEmail(email)) {
      throw new DomainException("Customer with email '" + email + "' already exists");
    }
    Customer customer = Customer.create(name, new Email(email));
    return customerRepository.save(customer);
  }

  @Override
  public Customer getCustomerById(UUID id) {
    return customerRepository.findById(id)
        .orElseThrow(() -> new CustomerNotFoundException(id));
  }

  @Override
  public List<Customer> getAllCustomers() {
    return customerRepository.findAll();
  }
}
