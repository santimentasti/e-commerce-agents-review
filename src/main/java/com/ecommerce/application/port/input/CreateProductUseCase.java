package com.ecommerce.application.port.input;

import com.ecommerce.domain.model.Product;
import java.math.BigDecimal;

public interface CreateProductUseCase {

  Product createProduct(String name, BigDecimal price, int stockQuantity);
}
