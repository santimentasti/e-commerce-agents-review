package com.ecommerce.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ecommerce.api.dto.ProductResponse;
import com.ecommerce.api.mapper.ProductMapper;
import com.ecommerce.application.port.input.CreateProductUseCase;
import com.ecommerce.application.port.input.GetProductUseCase;
import com.ecommerce.application.port.input.UpdateProductStockUseCase;
import com.ecommerce.domain.exception.ProductNotFoundException;
import com.ecommerce.domain.model.Price;
import com.ecommerce.domain.model.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private CreateProductUseCase createProductUseCase;

  @MockBean
  private GetProductUseCase getProductUseCase;

  @MockBean
  private UpdateProductStockUseCase updateProductStockUseCase;

  @MockBean
  private ProductMapper productMapper;

  @Test
  void createProduct_returns201WithProduct() throws Exception {
    UUID id = UUID.randomUUID();
    Product product = new Product(id, "Laptop", Price.of("999.99"), 10);
    ProductResponse response = new ProductResponse(id, "Laptop", new BigDecimal("999.99"), 10);
    when(createProductUseCase.createProduct(eq("Laptop"), any(BigDecimal.class), eq(10)))
        .thenReturn(product);
    when(productMapper.toResponse(product)).thenReturn(response);

    mockMvc.perform(post("/api/v1/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                Map.of("name", "Laptop", "price", "999.99", "stockQuantity", 10))))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Laptop"))
        .andExpect(jsonPath("$.stockQuantity").value(10));
  }

  @Test
  void createProduct_returns400WhenNameBlank() throws Exception {
    mockMvc.perform(post("/api/v1/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                Map.of("name", "", "price", "10.00", "stockQuantity", 1))))
        .andExpect(status().isBadRequest());
  }

  @Test
  void getProduct_returns404WhenNotFound() throws Exception {
    UUID id = UUID.randomUUID();
    when(getProductUseCase.getProductById(id))
        .thenThrow(new ProductNotFoundException(id));

    mockMvc.perform(get("/api/v1/products/" + id))
        .andExpect(status().isNotFound());
  }
}
