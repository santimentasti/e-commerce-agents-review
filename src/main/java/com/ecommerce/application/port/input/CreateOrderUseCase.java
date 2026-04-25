package com.ecommerce.application.port.input;

import com.ecommerce.domain.model.Order;
import java.util.Map;
import java.util.UUID;

public interface CreateOrderUseCase {

  /**
   * Creates and confirms an order for the given customer with the given product quantities.
   * productQuantities maps productId -> quantity.
   */
  Order createOrder(UUID customerId, Map<UUID, Integer> productQuantities);
}
