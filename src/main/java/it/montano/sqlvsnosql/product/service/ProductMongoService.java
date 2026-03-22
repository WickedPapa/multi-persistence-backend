package it.montano.sqlvsnosql.product.service;

import it.montano.sqlvsnosql.common.exeption.ResourceNotFoundException;
import it.montano.sqlvsnosql.common.mapper.ProductMapper;
import it.montano.sqlvsnosql.dto.ProductRequest;
import it.montano.sqlvsnosql.dto.ProductResponse;
import it.montano.sqlvsnosql.product.model.ProductDocument;
import it.montano.sqlvsnosql.product.repository.ProductMongoRepository;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app", name = "datasource", havingValue = "MONGODB")
public class ProductMongoService implements ProductService {

  private final ProductMongoRepository repo;
  private final ProductMapper mapper;

  @Override
  public @NonNull ProductResponse createProduct(@NonNull ProductRequest request) {
    ProductDocument doc = mapper.toDocument(request);
    return mapper.toResponse(repo.save(doc));
  }

  @Override
  public void deleteProduct(@NonNull UUID productId) {
    repo.deleteById(productId);
  }

  @Override
  public @NonNull ProductResponse getProductById(@NonNull UUID productId) {
    return repo.findById(productId).map(mapper::toResponse).orElseThrow(() ->  new ResourceNotFoundException(productId.toString()));
  }

  @Override
  public @NonNull List<ProductResponse> getProducts() {
    return repo.findAll().stream().map(mapper::toResponse).toList();
  }

  @Override
  public @NonNull ProductResponse updateProduct(
      @NonNull UUID productId, ProductRequest productRequest) {
    ProductDocument doc = repo.findById(productId).orElseThrow(() ->  new ResourceNotFoundException(productId.toString()));
    mapper.updateDocument(productRequest, doc);
    return mapper.toResponse(repo.save(doc));
  }
}
