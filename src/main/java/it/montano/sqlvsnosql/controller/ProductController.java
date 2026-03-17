package it.montano.sqlvsnosql.controller;

import it.montano.sqlvsnosql.api.ProductsApi;
import it.montano.sqlvsnosql.dto.ProductRequest;
import it.montano.sqlvsnosql.dto.ProductResponse;
import it.montano.sqlvsnosql.service.ProductService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductController implements ProductsApi {

  private final ProductService productService;

  @Override
  public ResponseEntity<ProductResponse> createProduct(ProductRequest productRequest) {
    return ResponseEntity.status(HttpStatus.CREATED).body(null);
  }

  @Override
  public ResponseEntity<List<ProductResponse>> getProducts() {
    return ResponseEntity.ok(null);
  }
}
