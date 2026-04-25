package com.ecommerce.application.port.input;

import com.ecommerce.domain.model.Customer;
import java.util.List;
import java.util.UUID;

public interface GetCustomerUseCase {

  Customer getCustomerById(UUID id);

  List<Customer> getAllCustomers();
}
