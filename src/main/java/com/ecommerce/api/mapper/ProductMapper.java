package com.ecommerce.api.mapper;

import com.ecommerce.api.dto.ProductResponse;
import com.ecommerce.domain.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

  @Mapping(target = "price", expression = "java(product.getPrice().amount())")
  ProductResponse toResponse(Product product);
}
