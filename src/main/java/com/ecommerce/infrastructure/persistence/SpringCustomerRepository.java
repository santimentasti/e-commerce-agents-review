package com.ecommerce.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SpringCustomerRepository extends JpaRepository<CustomerJpaEntity, UUID> {

  boolean existsByEmail(String email);
}
