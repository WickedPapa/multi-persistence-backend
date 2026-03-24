package it.montano.multipersistencebackend.order.controller;

import it.montano.multipersistencebackend.api.OrdersApi;
import it.montano.multipersistencebackend.common.util.UriUtil;
import it.montano.multipersistencebackend.dto.*;
import it.montano.multipersistencebackend.order.service.OrderService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderController implements OrdersApi {

  private final OrderService orderService;

  @Override
  public ResponseEntity<OrderResponse> createOrder(OrderRequest orderRequest) {
    OrderResponse orderResponse = orderService.createOrder(orderRequest);
    return ResponseEntity.created(
            UriUtil.buildUri(OrdersApi.PATH_GET_ORDER_BY_ID, orderResponse.getId()))
        .body(orderResponse);
  }

  @Override
  public ResponseEntity<Void> deleteOrder(UUID orderId) {
    orderService.deleteOrder(orderId);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<List<MostSoldProductResponse>> getMostSoldProducts() {
    return ResponseEntity.ok(orderService.getMostSoldProducts());
  }

  @Override
  public ResponseEntity<OrderResponse> getOrderById(UUID orderId) {
    return ResponseEntity.ok(orderService.getOrderById(orderId));
  }

  @Override
  public ResponseEntity<List<OrderResponse>> getOrdersByUserId(UUID userId) {
    return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
  }

  @Override
  public ResponseEntity<List<OrderResponse>> getOrders() {
    return ResponseEntity.ok(orderService.getOrders());
  }

  @Override
  public ResponseEntity<List<TotalSpentPerUserResponse>> getTotalSpentPerUser() {
    return ResponseEntity.ok(orderService.getTotalSpentPerUser());
  }
}
