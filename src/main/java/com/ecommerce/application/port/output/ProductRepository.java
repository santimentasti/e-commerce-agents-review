package com.ecommerce.application.port.output;

import com.ecommerce.domain.model.Product;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {

  Product save(Product product);

  Optional<Product> findById(UUID id);

  List<Product> findAll();

  void deleteById(UUID id);
}
