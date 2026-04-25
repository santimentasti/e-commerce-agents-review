package com.ecommerce.application.service;

import com.ecommerce.application.port.input.CreateProductUseCase;
import com.ecommerce.application.port.input.GetProductUseCase;
import com.ecommerce.application.port.input.UpdateProductStockUseCase;
import com.ecommerce.application.port.output.ProductRepository;
import com.ecommerce.domain.exception.ProductNotFoundException;
import com.ecommerce.domain.model.Price;
import com.ecommerce.domain.model.Product;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class ProductService
    implements CreateProductUseCase, GetProductUseCase, UpdateProductStockUseCase {

  private final ProductRepository productRepository;

  public ProductService(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  @Override
  public Product createProduct(String name, BigDecimal price, int stockQuantity) {
    Product product = Product.create(name, Price.of(price), stockQuantity);
    return productRepository.save(product);
  }

  @Override
  public Product getProductById(UUID id) {
    return productRepository.findById(id)
        .orElseThrow(() -> new ProductNotFoundException(id));
  }

  @Override
  public List<Product> getAllProducts() {
    return productRepository.findAll();
  }

  @Override
  public Product increaseStock(UUID productId, int quantity) {
    Product product = getProductById(productId);
    product.increaseStock(quantity);
    return productRepository.save(product);
  }

  @Override
  public Product decreaseStock(UUID productId, int quantity) {
    Product product = getProductById(productId);
    product.decreaseStock(quantity);
    return productRepository.save(product);
  }
}
