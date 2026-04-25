package com.ecommerce.application.port.input;

import com.ecommerce.domain.model.Order;
import java.util.List;
import java.util.UUID;

public interface GetOrderUseCase {

  Order getOrderById(UUID id);

  List<Order> getOrdersByCustomer(UUID customerId);
}
