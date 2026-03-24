package it.montano.multipersistencebackend.product.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import it.montano.multipersistencebackend.api.ProductsApi;
import it.montano.multipersistencebackend.common.util.UriUtil;
import it.montano.multipersistencebackend.config.ConfiguredTest;
import it.montano.multipersistencebackend.dto.ProductRequest;
import it.montano.multipersistencebackend.dto.ProductResponse;
import it.montano.multipersistencebackend.product.service.ProductService;
import java.util.List;
import java.util.UUID;
import org.instancio.junit.Given;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ConfiguredTest
class ProductControllerTest {

  @Mock ProductService productService;

  ProductController controller;

  @BeforeEach
  void setUp() {
    controller = new ProductController(productService);
  }

  @Test
  void shouldCreateProduct(@Given ProductRequest request, @Given ProductResponse response) {
    when(productService.createProduct(request)).thenReturn(response);

    ResponseEntity<ProductResponse> result = controller.createProduct(request);

    assertThat(result)
        .isNotNull()
        .satisfies(r -> assertThat(r.getStatusCode()).isEqualTo(HttpStatus.CREATED))
        .satisfies(
            r ->
                assertThat(r.getHeaders().getLocation())
                    .hasToString(
                        UriUtil.buildUri(ProductsApi.PATH_GET_PRODUCT_BY_ID, response.getId())
                            .toString()))
        .extracting(ResponseEntity::getBody)
        .isEqualTo(response);
  }

  @Test
  void shouldDeleteProduct(@Given UUID productId) {
    ResponseEntity<Void> result = controller.deleteProduct(productId);

    assertThat(result)
        .isNotNull()
        .extracting(ResponseEntity::getStatusCode)
        .isEqualTo(HttpStatus.NO_CONTENT);

    verify(productService).deleteProduct(productId);
  }

  @Test
  void shouldGetProductById(@Given UUID productId, @Given ProductResponse response) {

    when(productService.getProductById(productId)).thenReturn(response);

    ResponseEntity<ProductResponse> result = controller.getProductById(productId);

    assertThat(result)
        .isNotNull()
        .satisfies(r -> assertThat(r.getStatusCode()).isEqualTo(HttpStatus.OK))
        .extracting(ResponseEntity::getBody)
        .isEqualTo(response);
  }

  @Test
  void shouldGetProducts(@Given List<ProductResponse> responses) {
    when(productService.getProducts()).thenReturn(responses);

    ResponseEntity<List<ProductResponse>> result = controller.getProducts();

    assertThat(result)
        .isNotNull()
        .satisfies(r -> assertThat(r.getStatusCode()).isEqualTo(HttpStatus.OK))
        .extracting(ResponseEntity::getBody)
        .isEqualTo(responses);
  }

  @Test
  void shouldUpdateProduct(
      @Given UUID productId, @Given ProductRequest request, @Given ProductResponse response) {

    when(productService.updateProduct(productId, request)).thenReturn(response);

    ResponseEntity<ProductResponse> result = controller.updateProduct(productId, request);

    assertThat(result)
        .isNotNull()
        .satisfies(r -> assertThat(r.getStatusCode()).isEqualTo(HttpStatus.OK))
        .extracting(ResponseEntity::getBody)
        .isEqualTo(response);
  }
}
