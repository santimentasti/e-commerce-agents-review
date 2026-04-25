package com.ecommerce.application.port.input;

import com.ecommerce.domain.model.Product;
import java.util.UUID;

public interface UpdateProductStockUseCase {

  Product increaseStock(UUID productId, int quantity);

  Product decreaseStock(UUID productId, int quantity);
}
