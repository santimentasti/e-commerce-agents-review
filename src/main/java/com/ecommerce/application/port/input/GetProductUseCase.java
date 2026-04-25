package com.ecommerce.application.port.input;

import com.ecommerce.domain.model.Product;
import java.util.List;
import java.util.UUID;

public interface GetProductUseCase {

  Product getProductById(UUID id);

  List<Product> getAllProducts();
}
