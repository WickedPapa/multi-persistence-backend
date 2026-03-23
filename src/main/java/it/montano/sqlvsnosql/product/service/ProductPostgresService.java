package it.montano.sqlvsnosql.product.service;

import it.montano.sqlvsnosql.common.exeption.ResourceNotFoundException;
import it.montano.sqlvsnosql.common.mapper.ProductMapper;
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

  @CacheEvict(value = "products", allEntries = true)
  @Override
  public @NonNull ProductResponse createProduct(@NonNull ProductRequest request) {
    ProductEntity entity = mapper.toEntity(request);
    return mapper.toResponse(repo.save(entity));
  }

  @CacheEvict(value = "products", key = "#productId")
  @Override
  public void deleteProduct(@NonNull UUID productId) {
    repo.deleteById(productId);
  }

  @Cacheable(value = "products", key = "#productId")
  @Override
  public @NonNull ProductResponse getProductById(@NonNull UUID productId) {
    return repo.findById(productId)
        .map(mapper::toResponse)
        .orElseThrow(() -> new ResourceNotFoundException(productId.toString()));
  }

  @Override
  public @NonNull List<ProductResponse> getProducts() {
    return repo.findAll().stream().map(mapper::toResponse).toList();
  }

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
