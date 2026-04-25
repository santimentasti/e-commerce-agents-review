package com.ecommerce.infrastructure.config;

import com.ecommerce.application.port.output.CustomerRepository;
import com.ecommerce.application.port.output.OrderRepository;
import com.ecommerce.application.port.output.ProductRepository;
import com.ecommerce.application.service.CustomerService;
import com.ecommerce.application.service.OrderService;
import com.ecommerce.application.service.ProductService;
import com.ecommerce.infrastructure.persistence.CustomerRepositoryAdapter;
import com.ecommerce.infrastructure.persistence.OrderRepositoryAdapter;
import com.ecommerce.infrastructure.persistence.ProductRepositoryAdapter;
import com.ecommerce.infrastructure.persistence.SpringCustomerRepository;
import com.ecommerce.infrastructure.persistence.SpringOrderRepository;
import com.ecommerce.infrastructure.persistence.SpringProductRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

  @Bean
  public ProductRepository productRepository(SpringProductRepository springRepo) {
    return new ProductRepositoryAdapter(springRepo);
  }

  @Bean
  public CustomerRepository customerRepository(SpringCustomerRepository springRepo) {
    return new CustomerRepositoryAdapter(springRepo);
  }

  @Bean
  public OrderRepository orderRepository(
      SpringOrderRepository springRepo,
      CustomerRepository customerRepository,
      ProductRepository productRepository) {
    return new OrderRepositoryAdapter(springRepo, customerRepository, productRepository);
  }

  @Bean
  public ProductService productService(ProductRepository productRepository) {
    return new ProductService(productRepository);
  }

  @Bean
  public CustomerService customerService(CustomerRepository customerRepository) {
    return new CustomerService(customerRepository);
  }

  @Bean
  public OrderService orderService(
      OrderRepository orderRepository,
      CustomerRepository customerRepository,
      ProductRepository productRepository) {
    return new OrderService(orderRepository, customerRepository, productRepository);
  }
}
