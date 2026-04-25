package com.ecommerce.infrastructure.persistence;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringOrderRepository extends JpaRepository<OrderJpaEntity, UUID> {

  List<OrderJpaEntity> findByCustomerId(UUID customerId);
}
