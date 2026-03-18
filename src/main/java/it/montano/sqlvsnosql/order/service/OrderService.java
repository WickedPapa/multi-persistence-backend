package it.montano.sqlvsnosql.order.service;

import it.montano.sqlvsnosql.dto.OrderRequest;
import it.montano.sqlvsnosql.dto.OrderResponse;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;

public interface OrderService {

  @NonNull
  OrderResponse createOrder(@NonNull OrderRequest request);

  void deleteOrder(@NonNull UUID orderId);

  @NonNull
  OrderResponse getOrderById(@NonNull UUID orderId);

  @NonNull
  List<OrderResponse> getOrdersByUserId(@NonNull UUID userId);

  @NonNull
  List<OrderResponse> getOrders();
}
