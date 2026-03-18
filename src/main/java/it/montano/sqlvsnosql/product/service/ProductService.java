package it.montano.sqlvsnosql.product.service;

import it.montano.sqlvsnosql.dto.ProductRequest;
import it.montano.sqlvsnosql.dto.ProductResponse;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;

public interface ProductService {

  @NonNull
  ProductResponse createProduct(@NonNull ProductRequest request);

  void deleteProduct(@NonNull UUID productId);

  @NonNull
  ProductResponse getProductById(@NonNull UUID productId);

  @NonNull
  List<ProductResponse> getProducts();

  @NonNull
  ProductResponse updateProduct(@NonNull UUID productId, ProductRequest productRequest);

  @NonNull
  Double getProductPrice(@NonNull UUID productId);
}
