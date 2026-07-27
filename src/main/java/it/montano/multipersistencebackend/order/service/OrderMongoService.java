package it.montano.multipersistencebackend.order.service;

import it.montano.multipersistencebackend.common.annotation.ConditionalOnDatasource;
import it.montano.multipersistencebackend.common.constant.Datasources;
import it.montano.multipersistencebackend.common.dto.OrderItemRequestDto;
import it.montano.multipersistencebackend.common.dto.OrderRequestDto;
import it.montano.multipersistencebackend.common.mapper.OrderMapper;
import it.montano.multipersistencebackend.config.exeption.ResourceNotFoundException;
import it.montano.multipersistencebackend.dto.*;
import it.montano.multipersistencebackend.order.model.OrderDocument;
import it.montano.multipersistencebackend.order.repository.OrderMongoRepository;
import it.montano.multipersistencebackend.product.service.ProductService;
import it.montano.multipersistencebackend.user.service.UserService;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnDatasource(Datasources.MONGO)
public class OrderMongoService implements OrderService {

  private final UserService userService;
  private final ProductService productService;
  private final OrderMongoRepository repo;
  private final OrderMapper mapper;
  private final CacheManager cacheManager;

  /**
   * Creates an order in MongoDB after enriching prices and user info.
   *
   * <p>In this demo Mongo path we do not use a multi-document transaction boundary. Enrichment and
   * persistence are executed as regular repository operations.
   *
   * @param request API order payload
   * @return persisted order response
   */
  @CacheEvict(value = "orders-by-user", key = "#request.userId")
  @Override
  public @NonNull OrderResponse createOrder(@NonNull OrderRequest request) {
    OrderRequestDto orderRequestDto = mapper.toDto(request);
    enrichOrderWithUser(orderRequestDto);
    enrichOrderItems(orderRequestDto);
    OrderDocument saved = repo.save(mapper.toDocument(orderRequestDto));
    return mapper.toResponse(saved);
  }

  /**
   * Deletes an order and clears related caches.
   *
   * @param orderId identifier of the order to delete
   */
  @Override
  public void deleteOrder(@NonNull UUID orderId) {
    OrderDocument order =
        repo.findById(orderId)
            .orElseThrow(
                () -> new ResourceNotFoundException("Order not found with id " + orderId));
    UUID userId = order.getUser().getUserId();

    repo.deleteById(orderId);
    evictCache("orders", orderId);
    evictCache("orders-by-user", userId);
  }

  /**
   * Returns aggregate stats of the most sold products from MongoDB.
   *
   * @return ordered list by quantity sold
   */
  @Override
  public @NonNull List<MostSoldProductResponse> getMostSoldProducts() {
    return repo.getMostSoldProduct();
  }

  /**
   * Retrieves a single order by id with caching.
   *
   * @param orderId identifier of the order
   * @return found order response
   */
  @Cacheable(value = "orders", key = "#orderId")
  @Override
  public @NonNull OrderResponse getOrderById(@NonNull UUID orderId) {
    return repo.findById(orderId)
        .map(mapper::toResponse)
        .orElseThrow(
            () -> new ResourceNotFoundException("Order not found with id " + orderId));
  }

  /**
   * Lists orders for a specific user with caching.
   *
   * @param userId identifier of the user
   * @return orders placed by the user
   */
  @Cacheable(value = "orders-by-user", key = "#userId")
  @Override
  public @NonNull List<OrderResponse> getOrdersByUserId(@NonNull UUID userId) {
    return repo.findByUserUserId(userId).stream().map(mapper::toResponse).toList();
  }

  /**
   * Lists all orders stored in MongoDB.
   *
   * @return every order response
   */
  @Override
  public @NonNull List<OrderResponse> getOrders() {
    return repo.findAll().stream().map(mapper::toResponse).toList();
  }

  /**
   * Returns the sum spent per user computed via aggregation.
   *
   * @return spending summary
   */
  @Override
  public @NonNull List<TotalSpentPerUserResponse> getTotalSpentPerUser() {
    return repo.getTotalSpentPerUser();
  }

  private void enrichOrderWithUser(@NonNull OrderRequestDto request) {
    // Demo assumption: writes happen only through this app, so cached reads are acceptable here.
    // If external tools update DB data, order snapshots could be stale and cache should be
    // bypassed.
    UserResponse response = userService.getUserById(request.getUserId());
    request.setFirstName(response.getFirstName());
    request.setLastName(response.getLastName());
    request.setEmail(response.getEmail());
  }

  private void enrichOrderItems(@NonNull OrderRequestDto orderRequestDto) {
    orderRequestDto.getItems().forEach(this::fillItem);
  }

  private void fillItem(@NonNull OrderItemRequestDto orderItemRequestDto) {
    // Same assumption as above for product snapshots used during order creation.
    ProductResponse productResponse =
        productService.getProductById(orderItemRequestDto.getProductId());
    orderItemRequestDto.setPrice(productResponse.getPrice());
    orderItemRequestDto.setName(productResponse.getName());
  }

  private void evictCache(@NonNull String cacheName, @NonNull Object key) {
    var cache = cacheManager.getCache(cacheName);
    if (cache != null) {
      cache.evict(key);
    }
  }
}
