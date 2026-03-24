package it.montano.multipersistencebackend.order.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import it.montano.multipersistencebackend.api.OrdersApi;
import it.montano.multipersistencebackend.common.util.UriUtil;
import it.montano.multipersistencebackend.config.ConfiguredTest;
import it.montano.multipersistencebackend.dto.*;
import it.montano.multipersistencebackend.order.service.OrderService;
import java.util.List;
import java.util.UUID;
import org.instancio.junit.Given;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ConfiguredTest
class OrderControllerTest {

  @Mock OrderService orderService;

  OrderController controller;

  @BeforeEach
  void setUp() {
    controller = new OrderController(orderService);
  }

  @Test
  void shouldCreateOrder(@Given OrderRequest request, @Given OrderResponse response) {
    when(orderService.createOrder(request)).thenReturn(response);

    ResponseEntity<OrderResponse> result = controller.createOrder(request);

    assertThat(result)
        .isNotNull()
        .satisfies(r -> assertThat(r.getStatusCode()).isEqualTo(HttpStatus.CREATED))
        .satisfies(
            r ->
                assertThat(r.getHeaders().getLocation())
                    .hasToString(
                        UriUtil.buildUri(OrdersApi.PATH_GET_ORDER_BY_ID, response.getId())
                            .toString()))
        .extracting(ResponseEntity::getBody)
        .isEqualTo(response);
  }

  @Test
  void shouldDeleteOrder(@Given UUID orderId) {
    ResponseEntity<Void> result = controller.deleteOrder(orderId);

    assertThat(result)
        .isNotNull()
        .extracting(ResponseEntity::getStatusCode)
        .isEqualTo(HttpStatus.NO_CONTENT);

    verify(orderService).deleteOrder(orderId);
  }

  @Test
  void shouldGetOrderById(@Given UUID orderId, @Given OrderResponse response) {
    when(orderService.getOrderById(orderId)).thenReturn(response);

    ResponseEntity<OrderResponse> result = controller.getOrderById(orderId);

    assertThat(result)
        .isNotNull()
        .satisfies(r -> assertThat(r.getStatusCode()).isEqualTo(HttpStatus.OK))
        .extracting(ResponseEntity::getBody)
        .isEqualTo(response);
  }

  @Test
  void shouldGetOrdersByUserId(@Given UUID userId, @Given List<OrderResponse> responses) {

    when(orderService.getOrdersByUserId(userId)).thenReturn(responses);

    ResponseEntity<List<OrderResponse>> result = controller.getOrdersByUserId(userId);

    assertThat(result)
        .isNotNull()
        .satisfies(r -> assertThat(r.getStatusCode()).isEqualTo(HttpStatus.OK))
        .extracting(ResponseEntity::getBody)
        .isEqualTo(responses);
  }

  @Test
  void shouldGetOrders(@Given List<OrderResponse> responses) {
    when(orderService.getOrders()).thenReturn(responses);

    ResponseEntity<List<OrderResponse>> result = controller.getOrders();

    assertThat(result)
        .isNotNull()
        .satisfies(r -> assertThat(r.getStatusCode()).isEqualTo(HttpStatus.OK))
        .extracting(ResponseEntity::getBody)
        .isEqualTo(responses);
  }

  @Test
  void shouldGetMostSoldProducts(@Given List<MostSoldProductResponse> responses) {

    when(orderService.getMostSoldProducts()).thenReturn(responses);

    ResponseEntity<List<MostSoldProductResponse>> result = controller.getMostSoldProducts();

    assertThat(result)
        .isNotNull()
        .satisfies(r -> assertThat(r.getStatusCode()).isEqualTo(HttpStatus.OK))
        .extracting(ResponseEntity::getBody)
        .isEqualTo(responses);
  }

  @Test
  void shouldGetTotalSpentPerUser(@Given List<TotalSpentPerUserResponse> totals) {

    when(orderService.getTotalSpentPerUser()).thenReturn(totals);

    ResponseEntity<List<TotalSpentPerUserResponse>> result = controller.getTotalSpentPerUser();

    assertThat(result)
        .isNotNull()
        .satisfies(r -> assertThat(r.getStatusCode()).isEqualTo(HttpStatus.OK))
        .extracting(ResponseEntity::getBody)
        .isEqualTo(totals);
  }
}
