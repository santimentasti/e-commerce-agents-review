package com.ecommerce.api.mapper;

import com.ecommerce.api.dto.CustomerResponse;
import com.ecommerce.domain.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

  @Mapping(target = "email", expression = "java(customer.getEmail().value())")
  CustomerResponse toResponse(Customer customer);
}
