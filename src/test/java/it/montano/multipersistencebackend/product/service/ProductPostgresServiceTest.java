package it.montano.multipersistencebackend.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import it.montano.multipersistencebackend.common.mapper.ProductMapper;
import it.montano.multipersistencebackend.config.ConfiguredTest;
import it.montano.multipersistencebackend.config.exeption.ResourceNotFoundException;
import it.montano.multipersistencebackend.dto.ProductRequest;
import it.montano.multipersistencebackend.dto.ProductResponse;
import it.montano.multipersistencebackend.product.model.ProductEntity;
import it.montano.multipersistencebackend.product.repository.ProductPostgresRepository;
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
class ProductPostgresServiceTest {

  @InjectMocks ProductPostgresService service;

  @Mock ProductPostgresRepository repo;

  @Spy ProductMapper mapper = Mappers.getMapper(ProductMapper.class);

  @Test
  void shouldCreateProduct(
      @Given ProductRequest request, @Given ProductEntity entity, @Given ProductResponse response) {

    when(mapper.toEntity(request)).thenReturn(entity);
    when(repo.save(entity)).thenReturn(entity);
    when(mapper.toResponse(entity)).thenReturn(response);

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
      @Given UUID productId, @Given ProductEntity entity, @Given ProductResponse response) {

    when(repo.findById(productId)).thenReturn(Optional.of(entity));
    when(mapper.toResponse(entity)).thenReturn(response);

    ProductResponse result = service.getProductById(productId);

    assertThat(result).isNotNull().isEqualTo(response);
  }

  @Test
  void getProductByIdShouldThrow(@Given UUID productId) {
    assertThrows(ResourceNotFoundException.class, () -> service.getProductById(productId));
  }

  @Test
  void shouldGetProducts(@Given List<ProductEntity> entities, @Given ProductResponse response) {

    when(repo.findAll()).thenReturn(entities);
    when(mapper.toResponse(any(ProductEntity.class))).thenReturn(response);

    List<ProductResponse> result = service.getProducts();

    assertThat(result).isNotNull().contains(response);
  }

  @Test
  void shouldUpdateProduct(
      @Given UUID productId,
      @Given ProductRequest request,
      @Given ProductEntity entity,
      @Given ProductResponse response) {

    when(repo.findById(productId)).thenReturn(Optional.of(entity));
    doNothing().when(mapper).updateEntity(request, entity);
    when(repo.save(entity)).thenReturn(entity);
    when(mapper.toResponse(entity)).thenReturn(response);

    ProductResponse result = service.updateProduct(productId, request);

    assertThat(result).isNotNull().isEqualTo(response);
  }

  @Test
  void updateProductByIdShouldThrow(@Given UUID productId, @Given ProductRequest request) {
    assertThrows(ResourceNotFoundException.class, () -> service.updateProduct(productId, request));
  }
}
