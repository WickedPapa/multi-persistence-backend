package it.montano.sqlvsnosql.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import it.montano.sqlvsnosql.common.mapper.ProductMapper;
import it.montano.sqlvsnosql.config.ConfiguredTest;
import it.montano.sqlvsnosql.dto.ProductRequest;
import it.montano.sqlvsnosql.dto.ProductResponse;
import it.montano.sqlvsnosql.product.model.ProductDocument;
import it.montano.sqlvsnosql.product.repository.ProductMongoRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.instancio.junit.Given;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

@ConfiguredTest
class ProductMongoServiceTest {

  @InjectMocks ProductMongoService service;

  @Mock ProductMongoRepository repo;
  @Mock ProductMapper mapper;

  @Test
  void shouldCreateProduct(
      @Given ProductRequest request,
      @Given ProductDocument document,
      @Given ProductResponse response) {

    when(mapper.toDocument(request)).thenReturn(document);
    when(repo.save(document)).thenReturn(document);
    when(mapper.toResponse(document)).thenReturn(response);

    ProductResponse result = service.createProduct(request);

    assertThat(result).isNotNull().isEqualTo(response);
  }

  @Test
  void shouldDeleteProduct(@Given UUID productId) {
    doNothing().when(repo).deleteById(productId);

    service.deleteProduct(productId);

    verify(repo).deleteById(productId);
  }

  @Test
  void shouldGetProductById(
      @Given UUID productId, @Given ProductDocument document, @Given ProductResponse response) {

    when(repo.findById(productId)).thenReturn(Optional.of(document));
    when(mapper.toResponse(document)).thenReturn(response);

    ProductResponse result = service.getProductById(productId);

    assertThat(result).isNotNull().isEqualTo(response);
  }

  @Test
  void shouldGetProducts(@Given List<ProductDocument> documents, @Given ProductResponse response) {

    when(repo.findAll()).thenReturn(documents);
    when(mapper.toResponse(any(ProductDocument.class))).thenReturn(response);

    List<ProductResponse> result = service.getProducts();

    assertThat(result).isNotNull().contains(response);
  }

  @Test
  void shouldUpdateProduct(
      @Given UUID productId,
      @Given ProductRequest request,
      @Given ProductDocument document,
      @Given ProductResponse response) {

    when(repo.findById(productId)).thenReturn(Optional.of(document));
    doNothing().when(mapper).updateDocument(request, document);
    when(repo.save(document)).thenReturn(document);
    when(mapper.toResponse(document)).thenReturn(response);

    ProductResponse result = service.updateProduct(productId, request);

    assertThat(result).isNotNull().isEqualTo(response);
  }
}
