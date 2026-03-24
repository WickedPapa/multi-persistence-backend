package it.montano.multipersistencebackend.product.service;

import it.montano.multipersistencebackend.common.mapper.ProductMapper;
import it.montano.multipersistencebackend.config.exeption.ResourceNotFoundException;
import it.montano.multipersistencebackend.dto.ProductRequest;
import it.montano.multipersistencebackend.dto.ProductResponse;
import it.montano.multipersistencebackend.product.model.ProductDocument;
import it.montano.multipersistencebackend.product.repository.ProductMongoRepository;
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
@ConditionalOnProperty(prefix = "app", name = "datasource", havingValue = "MONGODB")
public class ProductMongoService implements ProductService {

  private final ProductMongoRepository repo;
  private final ProductMapper mapper;

  /**
   * Creates a product document in MongoDB.
   *
   * @param request API product payload
   * @return persisted product response
   */
  @CacheEvict(value = "products", allEntries = true)
  @Override
  public @NonNull ProductResponse createProduct(@NonNull ProductRequest request) {
    ProductDocument doc = mapper.toDocument(request);
    return mapper.toResponse(repo.save(doc));
  }

  /**
   * Deletes a product and clears its cache entry.
   *
   * @param productId identifier of the product
   */
  @CacheEvict(value = "products", key = "#productId")
  @Override
  public void deleteProduct(@NonNull UUID productId) {
    repo.deleteById(productId);
  }

  /**
   * Retrieves a product by id leveraging cache.
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
   * Lists every product stored in MongoDB.
   *
   * @return list of product responses
   */
  @Override
  public @NonNull List<ProductResponse> getProducts() {
    return repo.findAll().stream().map(mapper::toResponse).toList();
  }

  /**
   * Updates an existing product document.
   *
   * @param productId identifier of the product to update
   * @param productRequest new data to apply
   * @return updated product response
   */
  @CachePut(value = "products", key = "#productId")
  @Override
  public @NonNull ProductResponse updateProduct(
      @NonNull UUID productId, ProductRequest productRequest) {
    ProductDocument doc =
        repo.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException(productId.toString()));
    mapper.updateDocument(productRequest, doc);
    return mapper.toResponse(repo.save(doc));
  }
}
