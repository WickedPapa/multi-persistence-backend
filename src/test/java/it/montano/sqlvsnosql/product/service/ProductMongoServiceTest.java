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
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

@ConfiguredTest
class ProductMongoServiceTest {

  @InjectMocks ProductMongoService service;

  @Mock ProductMongoRepository repo;

  @Spy ProductMapper mapper = Mappers.getMapper(ProductMapper.class);

  @Test
  void shouldCreateProduct(@Given ProductRequest request) {
    when(repo.save(any(ProductDocument.class)))
        .thenAnswer(
            invocation -> {
              ProductDocument document = invocation.getArgument(0);
              document.setId(UUID.randomUUID());
              return document;
            });

    ProductResponse response = service.createProduct(request);

    assertThat(response)
        .isNotNull()
        .extracting(ProductResponse::getName, ProductResponse::getPrice)
        .containsExactly(request.getName(), request.getPrice());
  }

  @Test
  void shouldDeleteProduct(@Given UUID productId) {
    service.deleteProduct(productId);

    verify(repo).deleteById(productId);
  }

  @Test
  void shouldGetProductById() {
    UUID productId = UUID.randomUUID();
    ProductDocument document = new ProductDocument(productId, "Desk", 300.0);
    when(repo.findById(productId)).thenReturn(Optional.of(document));

    ProductResponse response = service.getProductById(productId);

    assertThat(response)
        .isNotNull()
        .extracting(ProductResponse::getId, ProductResponse::getName)
        .containsExactly(productId, "Desk");
  }

  @Test
  void shouldGetProducts() {
    List<ProductDocument> documents =
        List.of(
            new ProductDocument(UUID.randomUUID(), "Chair", 120.0),
            new ProductDocument(UUID.randomUUID(), "Lamp", 30.0));
    when(repo.findAll()).thenReturn(documents);

    List<ProductResponse> responses = service.getProducts();

    assertThat(responses).hasSize(2).extracting(ProductResponse::getName).contains("Chair", "Lamp");
  }

  @Test
  void shouldUpdateProduct(@Given ProductRequest request) {
    UUID productId = UUID.randomUUID();
    when(repo.save(any(ProductDocument.class))).thenAnswer(invocation -> invocation.getArgument(0));

    ProductResponse response = service.updateProduct(productId, request);

    assertThat(response)
        .isNotNull()
        .extracting(ProductResponse::getId, ProductResponse::getName)
        .containsExactly(productId, request.getName());
  }
}
