package it.montano.multipersistencebackend.product.controller;

import it.montano.multipersistencebackend.api.ProductsApi;
import it.montano.multipersistencebackend.common.util.UriUtil;
import it.montano.multipersistencebackend.dto.ProductRequest;
import it.montano.multipersistencebackend.dto.ProductResponse;
import it.montano.multipersistencebackend.product.service.ProductService;
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
    productService.deleteProduct(productId);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<ProductResponse> getProductById(UUID productId) {
    return ResponseEntity.ok(productService.getProductById(productId));
  }

  @Override
  public ResponseEntity<List<ProductResponse>> getProducts() {
    return ResponseEntity.ok(productService.getProducts());
  }

  @Override
  public ResponseEntity<ProductResponse> updateProduct(
      UUID productId, ProductRequest productRequest) {
    return ResponseEntity.ok(productService.updateProduct(productId, productRequest));
  }
}
