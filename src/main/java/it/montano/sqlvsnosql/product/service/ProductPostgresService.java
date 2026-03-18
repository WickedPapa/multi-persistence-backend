package it.montano.sqlvsnosql.product.service;

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
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app", name = "datasource", havingValue = "POSTGRES")
public class ProductPostgresService implements ProductService {

  private final ProductPostgresRepository repo;
  private final ProductMapper mapper;

  @Override
  public @NonNull ProductResponse createProduct(@NonNull ProductRequest request) {
    ProductEntity entity = mapper.toEntity(request);
    return mapper.toResponse(repo.save(entity));
  }

  @Override
  public void deleteProduct(@NonNull UUID productId) {
    repo.deleteById(productId);
  }

  @Override
  public @NonNull ProductResponse getProductById(@NonNull UUID productId) {
    return repo.findById(productId).map(mapper::toResponse).orElseThrow();
  }

  @Override
  public @NonNull List<ProductResponse> getProducts() {
    return repo.findAll().stream().map(mapper::toResponse).toList();
  }

  @Override
  public @NonNull ProductResponse updateProduct(
      @NonNull UUID productId, ProductRequest productRequest) {
    ProductEntity entity = mapper.toEntity(productRequest);
    entity.setId(productId);
    return mapper.toResponse(repo.save(entity));
  }
}
