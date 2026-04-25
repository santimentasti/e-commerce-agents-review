package com.ecommerce.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ecommerce.application.port.output.CustomerRepository;
import com.ecommerce.domain.exception.CustomerNotFoundException;
import com.ecommerce.domain.exception.DomainException;
import com.ecommerce.domain.model.Customer;
import com.ecommerce.domain.model.Email;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

  @Mock
  private CustomerRepository customerRepository;

  private CustomerService customerService;

  @BeforeEach
  void setUp() {
    customerService = new CustomerService(customerRepository);
  }

  @Test
  void createCustomer_savesAndReturnsCustomer() {
    when(customerRepository.existsByEmail("john@example.com")).thenReturn(false);
    Customer saved = new Customer(UUID.randomUUID(), "John", new Email("john@example.com"));
    when(customerRepository.save(any(Customer.class))).thenReturn(saved);

    Customer result = customerService.createCustomer("John", "john@example.com");

    assertThat(result.getName()).isEqualTo("John");
    assertThat(result.getEmail().value()).isEqualTo("john@example.com");
  }

  @Test
  void createCustomer_throwsWhenEmailAlreadyExists() {
    when(customerRepository.existsByEmail("dup@example.com")).thenReturn(true);

    assertThatThrownBy(() -> customerService.createCustomer("Jane", "dup@example.com"))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("already exists");

    verify(customerRepository, never()).save(any());
  }

  @Test
  void getCustomerById_returnsCustomer() {
    UUID id = UUID.randomUUID();
    Customer customer = new Customer(id, "Alice", new Email("alice@example.com"));
    when(customerRepository.findById(id)).thenReturn(Optional.of(customer));

    Customer result = customerService.getCustomerById(id);

    assertThat(result.getId()).isEqualTo(id);
  }

  @Test
  void getCustomerById_throwsWhenNotFound() {
    UUID id = UUID.randomUUID();
    when(customerRepository.findById(id)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> customerService.getCustomerById(id))
        .isInstanceOf(CustomerNotFoundException.class);
  }

  @Test
  void getAllCustomers_returnsList() {
    List<Customer> customers = List.of(
        new Customer(UUID.randomUUID(), "A", new Email("a@example.com")),
        new Customer(UUID.randomUUID(), "B", new Email("b@example.com")));
    when(customerRepository.findAll()).thenReturn(customers);

    List<Customer> result = customerService.getAllCustomers();

    assertThat(result).hasSize(2);
  }
}
