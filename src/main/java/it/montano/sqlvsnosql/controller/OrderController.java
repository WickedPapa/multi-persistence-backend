package it.montano.sqlvsnosql.controller;

import it.montano.sqlvsnosql.api.OrdersApi;
import it.montano.sqlvsnosql.dto.OrderRequest;
import it.montano.sqlvsnosql.dto.OrderResponse;
import it.montano.sqlvsnosql.service.OrderService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderController implements OrdersApi {

  private final OrderService orderService;

  @Override
  public ResponseEntity<OrderResponse> createOrder(OrderRequest orderRequest) {
    return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(orderRequest));
  }

  @Override
  public ResponseEntity<Void> deleteOrder(String orderId) {
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<List<OrderResponse>> getOrders() {
    return ResponseEntity.ok(orderService.getOrders());
  }
}
