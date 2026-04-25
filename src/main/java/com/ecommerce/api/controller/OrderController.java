package com.ecommerce.api.controller;

import com.ecommerce.api.dto.CreateOrderRequest;
import com.ecommerce.api.dto.OrderResponse;
import com.ecommerce.api.mapper.OrderMapper;
import com.ecommerce.application.port.input.CreateOrderUseCase;
import com.ecommerce.application.port.input.GetOrderUseCase;
import com.ecommerce.application.port.input.UpdateOrderStatusUseCase;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

  private final CreateOrderUseCase createOrderUseCase;
  private final GetOrderUseCase getOrderUseCase;
  private final UpdateOrderStatusUseCase updateOrderStatusUseCase;
  private final OrderMapper orderMapper;

  public OrderController(
      CreateOrderUseCase createOrderUseCase,
      GetOrderUseCase getOrderUseCase,
      UpdateOrderStatusUseCase updateOrderStatusUseCase,
      OrderMapper orderMapper) {
    this.createOrderUseCase = createOrderUseCase;
    this.getOrderUseCase = getOrderUseCase;
    this.updateOrderStatusUseCase = updateOrderStatusUseCase;
    this.orderMapper = orderMapper;
  }

  @PostMapping
  public ResponseEntity<OrderResponse> createOrder(
      @Valid @RequestBody CreateOrderRequest request) {
    var order = createOrderUseCase.createOrder(
        request.customerId(), request.productQuantities());
    OrderResponse response = orderMapper.toResponse(order);
    return ResponseEntity.created(URI.create("/api/v1/orders/" + order.getId()))
        .body(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<OrderResponse> getOrder(@PathVariable UUID id) {
    return ResponseEntity.ok(orderMapper.toResponse(getOrderUseCase.getOrderById(id)));
  }

  @GetMapping
  public ResponseEntity<List<OrderResponse>> getOrdersByCustomer(
      @RequestParam UUID customerId) {
    List<OrderResponse> orders = getOrderUseCase.getOrdersByCustomer(customerId).stream()
        .map(orderMapper::toResponse)
        .collect(Collectors.toList());
    return ResponseEntity.ok(orders);
  }

  @PutMapping("/{id}/ship")
  public ResponseEntity<OrderResponse> shipOrder(@PathVariable UUID id) {
    return ResponseEntity.ok(orderMapper.toResponse(updateOrderStatusUseCase.shipOrder(id)));
  }

  @PutMapping("/{id}/deliver")
  public ResponseEntity<OrderResponse> deliverOrder(@PathVariable UUID id) {
    return ResponseEntity.ok(orderMapper.toResponse(updateOrderStatusUseCase.deliverOrder(id)));
  }

  @PutMapping("/{id}/cancel")
  public ResponseEntity<OrderResponse> cancelOrder(@PathVariable UUID id) {
    return ResponseEntity.ok(orderMapper.toResponse(updateOrderStatusUseCase.cancelOrder(id)));
  }
}
