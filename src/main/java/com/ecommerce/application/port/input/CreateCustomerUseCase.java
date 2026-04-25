package com.ecommerce.application.port.input;

import com.ecommerce.domain.model.Customer;

public interface CreateCustomerUseCase {

  Customer createCustomer(String name, String email);
}
