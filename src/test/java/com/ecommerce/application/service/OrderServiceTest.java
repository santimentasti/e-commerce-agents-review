package com.ecommerce.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.ecommerce.application.port.output.CustomerRepository;
import com.ecommerce.application.port.output.OrderRepository;
import com.ecommerce.application.port.output.ProductRepository;
import com.ecommerce.domain.exception.CustomerNotFoundException;
import com.ecommerce.domain.exception.DomainException;
import com.ecommerce.domain.exception.OrderNotFoundException;
import com.ecommerce.domain.model.Customer;
import com.ecommerce.domain.model.Email;
import com.ecommerce.domain.model.Order;
import com.ecommerce.domain.model.Price;
import com.ecommerce.domain.model.Product;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

  @Mock
  private OrderRepository orderRepository;
  @Mock
  private CustomerRepository customerRepository;
  @Mock
  private ProductRepository productRepository;

  private OrderService orderService;

  private Customer customer;
  private Product product;

  @BeforeEach
  void setUp() {
    orderService = new OrderService(orderRepository, customerRepository, productRepository);
    customer = new Customer(UUID.randomUUID(), "Alice", new Email("alice@example.com"));
    product = new Product(UUID.randomUUID(), "Book", Price.of("15.00"), 100);
  }

  @Test
  void createOrder_buildsAndSavesConfirmedOrder() {
    when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
    when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
    when(productRepository.save(any())).thenReturn(product);
    when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    Order result = orderService.createOrder(
        customer.getId(), Map.of(product.getId(), 2));

    assertThat(result.getStatus()).isEqualTo(Order.Status.CONFIRMED);
    assertThat(result.getItems()).hasSize(1);
    assertThat(result.getItems().get(0).getQuantity()).isEqualTo(2);
  }

  @Test
  void createOrder_throwsWhenCustomerNotFound() {
    UUID unknownId = UUID.randomUUID();
    when(customerRepository.findById(unknownId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> orderService.createOrder(unknownId, Map.of()))
        .isInstanceOf(CustomerNotFoundException.class);
  }

  @Test
  void getOrderById_throwsWhenNotFound() {
    UUID id = UUID.randomUUID();
    when(orderRepository.findById(id)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> orderService.getOrderById(id))
        .isInstanceOf(OrderNotFoundException.class);
  }

  @Test
  void getOrdersByCustomer_returnsList() {
    Order order = Order.create(customer);
    when(orderRepository.findByCustomerId(customer.getId())).thenReturn(List.of(order));

    List<Order> result = orderService.getOrdersByCustomer(customer.getId());

    assertThat(result).hasSize(1);
  }

  @Test
  void shipOrder_transitionsToShipped() {
    Order order = Order.create(customer);
    order.addItem(new com.ecommerce.domain.model.OrderItem(product, 1));
    order.confirm();
    when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
    when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    Order result = orderService.shipOrder(order.getId());

    assertThat(result.getStatus()).isEqualTo(Order.Status.SHIPPED);
  }

  @Test
  void cancelOrder_transitionsToCancelled() {
    Order order = Order.create(customer);
    when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
    when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    Order result = orderService.cancelOrder(order.getId());

    assertThat(result.getStatus()).isEqualTo(Order.Status.CANCELLED);
  }

  @Test
  void deliverOrder_transitionsToDelivered() {
    Order order = Order.create(customer);
    order.addItem(new com.ecommerce.domain.model.OrderItem(product, 1));
    order.confirm();
    order.ship();
    when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
    when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    Order result = orderService.deliverOrder(order.getId());

    assertThat(result.getStatus()).isEqualTo(Order.Status.DELIVERED);
  }

  @Test
  void confirmOrder_throwsWhenOrderHasNoItems() {
    Order order = Order.create(customer);
    when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

    assertThatThrownBy(() -> orderService.confirmOrder(order.getId()))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("no items");
  }
}
