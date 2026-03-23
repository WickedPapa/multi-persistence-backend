package it.montano.sqlvsnosql.product.service;

import it.montano.sqlvsnosql.common.mapper.ProductMapper;
import it.montano.sqlvsnosql.config.exeption.ResourceNotFoundException;
import it.montano.sqlvsnosql.dto.ProductRequest;
import it.montano.sqlvsnosql.dto.ProductResponse;
import it.montano.sqlvsnosql.product.model.ProductEntity;
import it.montano.sqlvsnosql.product.repository.ProductPostgresRepository;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app", name = "datasource", havingValue = "POSTGRES")
public class ProductPostgresService implements ProductService {

  private final ProductPostgresRepository repo;
  private final ProductMapper mapper;

  /**
   * Creates a product entity in Postgres.
   *
   * @param request API product payload
   * @return persisted product response
   */
  @CacheEvict(value = "products", allEntries = true)
  @Override
  public @NonNull ProductResponse createProduct(@NonNull ProductRequest request) {
    ProductEntity entity = mapper.toEntity(request);
    return mapper.toResponse(repo.save(entity));
  }

  /**
   * Deletes a product by id and evicts its cache entry.
   *
   * @param productId identifier of the product
   */
  @CacheEvict(value = "products", key = "#productId")
  @Override
  public void deleteProduct(@NonNull UUID productId) {
    repo.deleteById(productId);
  }

  /**
   * Retrieves a product by id leveraging caching.
   *
   * @param productId identifier of the product
   * @return found product response
   */
  @Cacheable(value = "products", key = "#productId")
  @Override
  public @NonNull ProductResponse getProductById(@NonNull UUID productId) {
    return repo.findById(productId)
        .map(mapper::toResponse)
        .orElseThrow(() -> new ResourceNotFoundException(productId.toString()));
  }

  /**
   * Lists every product stored in Postgres.
   *
   * @return list of product responses
   */
  @Override
  public @NonNull List<ProductResponse> getProducts() {
    return repo.findAll().stream().map(mapper::toResponse).toList();
  }

  /**
   * Updates a product entity with the given payload.
   *
   * @param productId identifier of the product
   * @param productRequest data to apply
   * @return updated product response
   */
  @CachePut(value = "products", key = "#productId")
  @Override
  public @NonNull ProductResponse updateProduct(
      @NonNull UUID productId, ProductRequest productRequest) {
    ProductEntity entity =
        repo.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException(productId.toString()));
    mapper.updateEntity(productRequest, entity);
    return mapper.toResponse(repo.save(entity));
  }
}
