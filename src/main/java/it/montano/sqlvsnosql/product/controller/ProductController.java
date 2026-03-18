package it.montano.sqlvsnosql.product.controller;

import it.montano.sqlvsnosql.api.ProductsApi;
import it.montano.sqlvsnosql.common.util.UriUtil;
import it.montano.sqlvsnosql.dto.ProductRequest;
import it.montano.sqlvsnosql.dto.ProductResponse;
import it.montano.sqlvsnosql.product.service.ProductService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductController implements ProductsApi {

  private final ProductService productService;

  @Override
  public ResponseEntity<ProductResponse> createProduct(ProductRequest request) {
    ProductResponse productResponse = productService.createProduct(request);
    return ResponseEntity.created(
            UriUtil.buildUri(ProductsApi.PATH_GET_PRODUCT_BY_ID, productResponse.getId()))
        .body(productResponse);
  }

  @Override
  public ResponseEntity<Void> deleteProduct(UUID productId) {
    return ProductsApi.super.deleteProduct(productId);
  }

  @Override
  public ResponseEntity<ProductResponse> getProductById(UUID productId) {
    return ProductsApi.super.getProductById(productId);
  }

  @Override
  public ResponseEntity<List<ProductResponse>> getProducts() {
    return ResponseEntity.ok(productService.getProducts());
  }

  @Override
  public ResponseEntity<ProductResponse> updateProduct(
      UUID productId, ProductRequest productRequest) {
    return ProductsApi.super.updateProduct(productId, productRequest);
  }
}
