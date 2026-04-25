package com.ecommerce.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SpringProductRepository extends JpaRepository<ProductJpaEntity, UUID> {
}
