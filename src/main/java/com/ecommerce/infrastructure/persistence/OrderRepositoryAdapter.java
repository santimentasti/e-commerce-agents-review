package com.ecommerce.infrastructure.persistence;

import com.ecommerce.application.port.output.CustomerRepository;
import com.ecommerce.application.port.output.OrderRepository;
import com.ecommerce.application.port.output.ProductRepository;
import com.ecommerce.domain.model.Customer;
import com.ecommerce.domain.model.Order;
import com.ecommerce.domain.model.OrderItem;
import com.ecommerce.domain.model.Price;
import com.ecommerce.domain.model.Product;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class OrderRepositoryAdapter implements OrderRepository {

  private final SpringOrderRepository springRepository;
  private final CustomerRepository customerRepository;
  private final ProductRepository productRepository;

  public OrderRepositoryAdapter(
      SpringOrderRepository springRepository,
      CustomerRepository customerRepository,
      ProductRepository productRepository) {
    this.springRepository = springRepository;
    this.customerRepository = customerRepository;
    this.productRepository = productRepository;
  }

  @Override
  public Order save(Order order) {
    OrderJpaEntity entity = toEntity(order);
    OrderJpaEntity saved = springRepository.save(entity);
    return toDomain(saved);
  }

  @Override
  public Optional<Order> findById(UUID id) {
    return springRepository.findById(id).map(this::toDomain);
  }

  @Override
  public List<Order> findByCustomerId(UUID customerId) {
    return springRepository.findByCustomerId(customerId).stream()
        .map(this::toDomain)
        .collect(Collectors.toList());
  }

  private OrderJpaEntity toEntity(Order order) {
    List<OrderItemEmbeddable> items = order.getItems().stream()
        .map(item -> new OrderItemEmbeddable(
            item.getProduct().getId(),
            item.getProduct().getName(),
            item.getQuantity(),
            item.getUnitPrice().amount()))
        .collect(Collectors.toList());

    OrderJpaEntity entity = new OrderJpaEntity(
        order.getId(),
        order.getCustomer().getId(),
        order.getCustomer().getName(),
        order.getCustomer().getEmail().value(),
        order.getStatus(),
        order.getCreatedAt(),
        items);
    return entity;
  }

  private Order toDomain(OrderJpaEntity entity) {
    Customer customer = customerRepository.findById(entity.getCustomerId())
        .orElseThrow(() -> new IllegalStateException(
            "Customer not found for order: " + entity.getId()));

    Order order = new Order(entity.getId(), customer);

    entity.getItems().forEach(itemEntity -> {
      Product product = productRepository.findById(itemEntity.getProductId())
          .orElseThrow(() -> new IllegalStateException(
              "Product not found for order item: " + itemEntity.getProductId()));
      order.addItem(new OrderItem(product, itemEntity.getQuantity()));
    });

    restoreOrderStatus(order, entity);
    return order;
  }

  private void restoreOrderStatus(Order order, OrderJpaEntity entity) {
    switch (entity.getStatus()) {
      case CONFIRMED -> order.confirm();
      case SHIPPED -> {
        order.confirm();
        order.ship();
      }
      case DELIVERED -> {
        order.confirm();
        order.ship();
        order.deliver();
      }
      case CANCELLED -> order.cancel();
      default -> {
        // PENDING: no transition needed
      }
    }
  }
}
