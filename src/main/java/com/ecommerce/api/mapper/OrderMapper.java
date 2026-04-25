package com.ecommerce.api.mapper;

import com.ecommerce.api.dto.OrderItemResponse;
import com.ecommerce.api.dto.OrderResponse;
import com.ecommerce.domain.model.Order;
import com.ecommerce.domain.model.OrderItem;
import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {

  default OrderResponse toResponse(Order order) {
    List<OrderItemResponse> items = order.getItems().stream()
        .map(this::toItemResponse)
        .collect(Collectors.toList());

    return new OrderResponse(
        order.getId(),
        order.getCustomer().getId(),
        order.getCustomer().getName(),
        items,
        order.getStatus().name(),
        order.totalAmount().amount(),
        order.getCreatedAt());
  }

  default OrderItemResponse toItemResponse(OrderItem item) {
    return new OrderItemResponse(
        item.getProduct().getId(),
        item.getProduct().getName(),
        item.getQuantity(),
        item.getUnitPrice().amount(),
        item.subtotal().amount());
  }
}
