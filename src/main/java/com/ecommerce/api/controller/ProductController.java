package com.ecommerce.api.controller;

import com.ecommerce.api.dto.ProductRequest;
import com.ecommerce.api.dto.ProductResponse;
import com.ecommerce.api.dto.StockUpdateRequest;
import com.ecommerce.api.mapper.ProductMapper;
import com.ecommerce.application.port.input.CreateProductUseCase;
import com.ecommerce.application.port.input.GetProductUseCase;
import com.ecommerce.application.port.input.UpdateProductStockUseCase;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

  private final CreateProductUseCase createProductUseCase;
  private final GetProductUseCase getProductUseCase;
  private final UpdateProductStockUseCase updateProductStockUseCase;
  private final ProductMapper productMapper;

  public ProductController(
      CreateProductUseCase createProductUseCase,
      GetProductUseCase getProductUseCase,
      UpdateProductStockUseCase updateProductStockUseCase,
      ProductMapper productMapper) {
    this.createProductUseCase = createProductUseCase;
    this.getProductUseCase = getProductUseCase;
    this.updateProductStockUseCase = updateProductStockUseCase;
    this.productMapper = productMapper;
  }

  @PostMapping
  public ResponseEntity<ProductResponse> createProduct(
      @Valid @RequestBody ProductRequest request) {
    var product = createProductUseCase.createProduct(
        request.name(), request.price(), request.stockQuantity());
    ProductResponse response = productMapper.toResponse(product);
    return ResponseEntity.created(URI.create("/api/v1/products/" + product.getId()))
        .body(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ProductResponse> getProduct(@PathVariable UUID id) {
    return ResponseEntity.ok(productMapper.toResponse(getProductUseCase.getProductById(id)));
  }

  @GetMapping
  public ResponseEntity<List<ProductResponse>> getAllProducts() {
    List<ProductResponse> products = getProductUseCase.getAllProducts().stream()
        .map(productMapper::toResponse)
        .collect(Collectors.toList());
    return ResponseEntity.ok(products);
  }

  @PutMapping("/{id}/stock/increase")
  public ResponseEntity<ProductResponse> increaseStock(
      @PathVariable UUID id,
      @Valid @RequestBody StockUpdateRequest request) {
    var product = updateProductStockUseCase.increaseStock(id, request.quantity());
    return ResponseEntity.ok(productMapper.toResponse(product));
  }

  @PutMapping("/{id}/stock/decrease")
  public ResponseEntity<ProductResponse> decreaseStock(
      @PathVariable UUID id,
      @Valid @RequestBody StockUpdateRequest request) {
    var product = updateProductStockUseCase.decreaseStock(id, request.quantity());
    return ResponseEntity.ok(productMapper.toResponse(product));
  }
}
