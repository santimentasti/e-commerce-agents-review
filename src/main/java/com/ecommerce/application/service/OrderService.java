package com.ecommerce.application.service;

import com.ecommerce.application.port.input.CreateOrderUseCase;
import com.ecommerce.application.port.input.GetOrderUseCase;
import com.ecommerce.application.port.input.UpdateOrderStatusUseCase;
import com.ecommerce.application.port.output.CustomerRepository;
import com.ecommerce.application.port.output.OrderRepository;
import com.ecommerce.application.port.output.ProductRepository;
import com.ecommerce.domain.exception.CustomerNotFoundException;
import com.ecommerce.domain.exception.OrderNotFoundException;
import com.ecommerce.domain.exception.ProductNotFoundException;
import com.ecommerce.domain.model.Customer;
import com.ecommerce.domain.model.Order;
import com.ecommerce.domain.model.OrderItem;
import com.ecommerce.domain.model.Product;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class OrderService
    implements CreateOrderUseCase, GetOrderUseCase, UpdateOrderStatusUseCase {

  private final OrderRepository orderRepository;
  private final CustomerRepository customerRepository;
  private final ProductRepository productRepository;

  public OrderService(
      OrderRepository orderRepository,
      CustomerRepository customerRepository,
      ProductRepository productRepository) {
    this.orderRepository = orderRepository;
    this.customerRepository = customerRepository;
    this.productRepository = productRepository;
  }

  @Override
  public Order createOrder(UUID customerId, Map<UUID, Integer> productQuantities) {
    Customer customer = customerRepository.findById(customerId)
        .orElseThrow(() -> new CustomerNotFoundException(customerId));

    Order order = Order.create(customer);

    productQuantities.forEach((productId, quantity) -> {
      Product product = productRepository.findById(productId)
          .orElseThrow(() -> new ProductNotFoundException(productId));
      product.decreaseStock(quantity);
      productRepository.save(product);
      order.addItem(new OrderItem(product, quantity));
    });

    order.confirm();
    return orderRepository.save(order);
  }

  @Override
  public Order getOrderById(UUID id) {
    return orderRepository.findById(id)
        .orElseThrow(() -> new OrderNotFoundException(id));
  }

  @Override
  public List<Order> getOrdersByCustomer(UUID customerId) {
    return orderRepository.findByCustomerId(customerId);
  }

  @Override
  public Order confirmOrder(UUID orderId) {
    Order order = getOrderById(orderId);
    order.confirm();
    return orderRepository.save(order);
  }

  @Override
  public Order shipOrder(UUID orderId) {
    Order order = getOrderById(orderId);
    order.ship();
    return orderRepository.save(order);
  }

  @Override
  public Order deliverOrder(UUID orderId) {
    Order order = getOrderById(orderId);
    order.deliver();
    return orderRepository.save(order);
  }

  @Override
  public Order cancelOrder(UUID orderId) {
    Order order = getOrderById(orderId);
    order.cancel();
    return orderRepository.save(order);
  }
}
