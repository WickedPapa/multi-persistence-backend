package it.montano.sqlvsnosql.order.service;

import it.montano.sqlvsnosql.common.dto.OrderItemRequestDto;
import it.montano.sqlvsnosql.common.dto.OrderRequestDto;
import it.montano.sqlvsnosql.common.mapper.OrderMapper;
import it.montano.sqlvsnosql.config.exeption.ResourceNotFoundException;
import it.montano.sqlvsnosql.dto.*;
import it.montano.sqlvsnosql.order.model.OrderEntity;
import it.montano.sqlvsnosql.order.repository.OrderPostgresRepository;
import it.montano.sqlvsnosql.product.service.ProductService;
import it.montano.sqlvsnosql.user.service.UserService;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app", name = "datasource", havingValue = "POSTGRES")
public class OrderPostgresService implements OrderService {

  private final UserService userService;
  private final ProductService productService;
  private final OrderPostgresRepository repo;
  private final OrderMapper mapper;

  /**
   * Persists a new order in Postgres after enriching items.
   *
   * @param request API order payload
   * @return persisted order response with hydrated user data
   */
  @CacheEvict(value = "orders-by-user", key = "#request.userId")
  @Override
  public @NonNull OrderResponse createOrder(@NonNull OrderRequest request) {
    OrderRequestDto orderItemRequestDto = mapper.toDto(request);
    enrichOrderItems(orderItemRequestDto);
    OrderEntity saved = repo.save(mapper.toEntity(orderItemRequestDto));
    return enrichOrderResponse(mapper.toResponse(saved));
  }

  /**
   * Deletes an order and invalidates caches.
   *
   * @param orderId identifier of the order to delete
   */
  @Caching(
      evict = {
        @CacheEvict(value = "orders-by-user", allEntries = true),
        @CacheEvict(value = "orders", key = "#orderId")
      })
  @Override
  public void deleteOrder(@NonNull UUID orderId) {
    repo.deleteById(orderId);
  }

  /**
   * Provides aggregate stats for the most sold products in Postgres.
   *
   * @return ordered list by quantity sold
   */
  @Override
  public @NonNull List<MostSoldProductResponse> getMostSoldProducts() {
    return repo.getMostSoldProduct();
  }

  /**
   * Retrieves an order by id and hydrates its user details.
   *
   * @param orderId identifier of the order
   * @return found order response
   */
  @Cacheable(value = "orders", key = "#orderId")
  @Override
  public @NonNull OrderResponse getOrderById(@NonNull UUID orderId) {
    return repo.findById(orderId)
        .map(mapper::toResponse)
        .map(this::enrichOrderResponse)
        .orElseThrow(() -> new ResourceNotFoundException(orderId.toString()));
  }

  /**
   * Lists orders for a user while caching the result set.
   *
   * @param userId identifier of the user
   * @return hydrated order responses
   */
  @Cacheable(value = "orders-by-user", key = "#userId")
  @Override
  public @NonNull List<OrderResponse> getOrdersByUserId(@NonNull UUID userId) {
    return repo.findByUserId(userId).stream()
        .map(mapper::toResponse)
        .map(this::enrichOrderResponse)
        .toList();
  }

  /**
   * Lists all orders stored in Postgres.
   *
   * @return hydrated order responses
   */
  @Override
  public @NonNull List<OrderResponse> getOrders() {
    return repo.findAll().stream().map(mapper::toResponse).map(this::enrichOrderResponse).toList();
  }

  /**
   * Summarizes total spent per user using database aggregation.
   *
   * @return spending summary
   */
  @Override
  public @NonNull List<TotalSpentPerUserResponse> getTotalSpentPerUser() {
    return repo.getTotalSpentPerUser();
  }

  private void enrichOrderItems(@NonNull OrderRequestDto orderRequestDto) {
    orderRequestDto.getItems().forEach(this::fillItemPrice);
  }

  private OrderResponse enrichOrderResponse(@NonNull OrderResponse orderResponse) {
    orderResponse.getItems().forEach(this::fillItemName);
    UserResponse userResponse = userService.getUserById(orderResponse.getUser().getUserId());
    orderResponse.setUser(mapper.toOrderUserResponse(userResponse));
    return orderResponse;
  }

  private void fillItemPrice(@NonNull OrderItemRequestDto orderItemRequestDto) {
    orderItemRequestDto.setPrice(
        productService.getProductById(orderItemRequestDto.getProductId()).getPrice());
  }

  private void fillItemName(@NonNull OrderItemResponse orderItemResponse) {
    orderItemResponse.setName(
        productService.getProductById(orderItemResponse.getProductId()).getName());
  }
}
