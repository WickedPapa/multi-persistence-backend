package it.montano.multipersistencebackend.order.service;

import it.montano.multipersistencebackend.dto.MostSoldProductResponse;
import it.montano.multipersistencebackend.dto.OrderRequest;
import it.montano.multipersistencebackend.dto.OrderResponse;
import it.montano.multipersistencebackend.dto.TotalSpentPerUserResponse;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;

public interface OrderService {

  /**
   * Creates a new order from the provided payload.
   *
   * @param request API order payload
   * @return created order response
   */
  @NonNull
  OrderResponse createOrder(@NonNull OrderRequest request);

  /**
   * Deletes an existing order.
   *
   * @param orderId identifier of the order
   */
  void deleteOrder(@NonNull UUID orderId);

  /**
   * Returns statistics for the most sold products.
   *
   * @return ordered list of products by quantity sold
   */
  @NonNull
  List<MostSoldProductResponse> getMostSoldProducts();

  /**
   * Retrieves a single order by its identifier.
   *
   * @param orderId identifier of the order
   * @return found order response
   */
  @NonNull
  OrderResponse getOrderById(@NonNull UUID orderId);

  /**
   * Returns all orders belonging to a user.
   *
   * @param userId identifier of the user
   * @return list of order responses
   */
  @NonNull
  List<OrderResponse> getOrdersByUserId(@NonNull UUID userId);

  /**
   * Lists every order in the system.
   *
   * @return list of order responses
   */
  @NonNull
  List<OrderResponse> getOrders();

  /**
   * Summarizes the total amount spent by each user.
   *
   * @return spending summary
   */
  @NonNull
  List<TotalSpentPerUserResponse> getTotalSpentPerUser();
}
