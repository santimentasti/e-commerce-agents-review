package com.ecommerce.application.port.input;

import com.ecommerce.domain.model.Order;
import java.util.UUID;

public interface UpdateOrderStatusUseCase {

  Order confirmOrder(UUID orderId);

  Order shipOrder(UUID orderId);

  Order deliverOrder(UUID orderId);

  Order cancelOrder(UUID orderId);
}
