package com.ecommerce.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ecommerce.application.port.output.ProductRepository;
import com.ecommerce.domain.exception.ProductNotFoundException;
import com.ecommerce.domain.model.Price;
import com.ecommerce.domain.model.Product;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

  @Mock
  private ProductRepository productRepository;

  private ProductService productService;

  @BeforeEach
  void setUp() {
    productService = new ProductService(productRepository);
  }

  @Test
  void createProduct_savesAndReturnsProduct() {
    Product savedProduct = new Product(UUID.randomUUID(), "Laptop",
        Price.of("999.99"), 10);
    when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

    Product result = productService.createProduct("Laptop", new BigDecimal("999.99"), 10);

    assertThat(result.getName()).isEqualTo("Laptop");
    assertThat(result.getStockQuantity()).isEqualTo(10);
    verify(productRepository).save(any(Product.class));
  }

  @Test
  void getProductById_returnsProduct_whenExists() {
    UUID id = UUID.randomUUID();
    Product product = new Product(id, "Phone", Price.of("499.00"), 5);
    when(productRepository.findById(id)).thenReturn(Optional.of(product));

    Product result = productService.getProductById(id);

    assertThat(result.getId()).isEqualTo(id);
  }

  @Test
  void getProductById_throwsProductNotFoundException_whenNotFound() {
    UUID id = UUID.randomUUID();
    when(productRepository.findById(id)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> productService.getProductById(id))
        .isInstanceOf(ProductNotFoundException.class);
  }

  @Test
  void getAllProducts_returnsList() {
    List<Product> products = List.of(
        new Product(UUID.randomUUID(), "A", Price.of("10.00"), 1),
        new Product(UUID.randomUUID(), "B", Price.of("20.00"), 2));
    when(productRepository.findAll()).thenReturn(products);

    List<Product> result = productService.getAllProducts();

    assertThat(result).hasSize(2);
  }

  @Test
  void increaseStock_updatesAndSaves() {
    UUID id = UUID.randomUUID();
    Product product = new Product(id, "Widget", Price.of("5.00"), 10);
    when(productRepository.findById(id)).thenReturn(Optional.of(product));
    when(productRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    Product result = productService.increaseStock(id, 5);

    assertThat(result.getStockQuantity()).isEqualTo(15);
    verify(productRepository).save(product);
  }

  @Test
  void decreaseStock_updatesAndSaves() {
    UUID id = UUID.randomUUID();
    Product product = new Product(id, "Widget", Price.of("5.00"), 10);
    when(productRepository.findById(id)).thenReturn(Optional.of(product));
    when(productRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    Product result = productService.decreaseStock(id, 3);

    assertThat(result.getStockQuantity()).isEqualTo(7);
  }

  @Test
  void decreaseStock_throwsWhenInsufficientStock() {
    UUID id = UUID.randomUUID();
    Product product = new Product(id, "Widget", Price.of("5.00"), 2);
    when(productRepository.findById(id)).thenReturn(Optional.of(product));

    assertThatThrownBy(() -> productService.decreaseStock(id, 5))
        .isInstanceOf(com.ecommerce.domain.exception.InsufficientStockException.class);
  }
}
