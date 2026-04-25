package com.ecommerce.infrastructure.persistence;

import com.ecommerce.application.port.output.ProductRepository;
import com.ecommerce.domain.model.Price;
import com.ecommerce.domain.model.Product;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class ProductRepositoryAdapter implements ProductRepository {

  private final SpringProductRepository springRepository;

  public ProductRepositoryAdapter(SpringProductRepository springRepository) {
    this.springRepository = springRepository;
  }

  @Override
  public Product save(Product product) {
    ProductJpaEntity entity = toEntity(product);
    ProductJpaEntity saved = springRepository.save(entity);
    return toDomain(saved);
  }

  @Override
  public Optional<Product> findById(UUID id) {
    return springRepository.findById(id).map(this::toDomain);
  }

  @Override
  public List<Product> findAll() {
    return springRepository.findAll().stream()
        .map(this::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public void deleteById(UUID id) {
    springRepository.deleteById(id);
  }

  private ProductJpaEntity toEntity(Product product) {
    return new ProductJpaEntity(
        product.getId(),
        product.getName(),
        product.getPrice().amount(),
        product.getStockQuantity());
  }

  private Product toDomain(ProductJpaEntity entity) {
    return new Product(
        entity.getId(),
        entity.getName(),
        Price.of(entity.getPrice()),
        entity.getStockQuantity());
  }
}
