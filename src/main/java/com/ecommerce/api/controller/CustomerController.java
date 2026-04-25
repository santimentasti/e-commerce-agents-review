package com.ecommerce.api.controller;

import com.ecommerce.api.dto.CustomerRequest;
import com.ecommerce.api.dto.CustomerResponse;
import com.ecommerce.api.mapper.CustomerMapper;
import com.ecommerce.application.port.input.CreateCustomerUseCase;
import com.ecommerce.application.port.input.GetCustomerUseCase;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

  private final CreateCustomerUseCase createCustomerUseCase;
  private final GetCustomerUseCase getCustomerUseCase;
  private final CustomerMapper customerMapper;

  public CustomerController(
      CreateCustomerUseCase createCustomerUseCase,
      GetCustomerUseCase getCustomerUseCase,
      CustomerMapper customerMapper) {
    this.createCustomerUseCase = createCustomerUseCase;
    this.getCustomerUseCase = getCustomerUseCase;
    this.customerMapper = customerMapper;
  }

  @PostMapping
  public ResponseEntity<CustomerResponse> createCustomer(
      @Valid @RequestBody CustomerRequest request) {
    var customer = createCustomerUseCase.createCustomer(request.name(), request.email());
    CustomerResponse response = customerMapper.toResponse(customer);
    return ResponseEntity.created(URI.create("/api/v1/customers/" + customer.getId()))
        .body(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<CustomerResponse> getCustomer(@PathVariable UUID id) {
    return ResponseEntity.ok(customerMapper.toResponse(getCustomerUseCase.getCustomerById(id)));
  }

  @GetMapping
  public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
    List<CustomerResponse> customers = getCustomerUseCase.getAllCustomers().stream()
        .map(customerMapper::toResponse)
        .collect(Collectors.toList());
    return ResponseEntity.ok(customers);
  }
}
