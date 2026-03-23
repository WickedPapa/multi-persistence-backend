package it.montano.sqlvsnosql.product.service;

import it.montano.sqlvsnosql.dto.ProductRequest;
import it.montano.sqlvsnosql.dto.ProductResponse;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;

public interface ProductService {

  /**
   * Creates a product from the provided payload.
   *
   * @param request API product payload
   * @return created product response
   */
  @NonNull
  ProductResponse createProduct(@NonNull ProductRequest request);

  /**
   * Deletes a product by its identifier.
   *
   * @param productId identifier of the product
   */
  void deleteProduct(@NonNull UUID productId);

  /**
   * Retrieves a product by id.
   *
   * @param productId identifier of the product
   * @return found product response
   */
  @NonNull
  ProductResponse getProductById(@NonNull UUID productId);

  /**
   * Lists every product in the catalog.
   *
   * @return list of product responses
   */
  @NonNull
  List<ProductResponse> getProducts();

  /**
   * Updates a product with the given payload.
   *
   * @param productId identifier of the product
   * @param productRequest data to apply
   * @return updated product response
   */
  @NonNull
  ProductResponse updateProduct(@NonNull UUID productId, ProductRequest productRequest);
}
