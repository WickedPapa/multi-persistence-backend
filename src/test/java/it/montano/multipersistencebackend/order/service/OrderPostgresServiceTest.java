package it.montano.multipersistencebackend.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import it.montano.multipersistencebackend.common.mapper.OrderMapper;
import it.montano.multipersistencebackend.config.ConfiguredTest;
import it.montano.multipersistencebackend.config.exeption.ResourceNotFoundException;
import it.montano.multipersistencebackend.dto.*;
import it.montano.multipersistencebackend.order.model.OrderEntity;
import it.montano.multipersistencebackend.order.repository.OrderPostgresRepository;
import it.montano.multipersistencebackend.product.service.ProductService;
import it.montano.multipersistencebackend.user.service.UserService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.instancio.junit.Given;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

@ConfiguredTest
class OrderPostgresServiceTest {

  @InjectMocks OrderPostgresService service;

  @Mock UserService userService;
  @Mock ProductService productService;
  @Mock OrderPostgresRepository repo;

  @Spy OrderMapper mapper = Mappers.getMapper(OrderMapper.class);

  @Test
  void shouldCreateOrder(
      @Given OrderRequest request,
      @Given ProductResponse product,
      @Given UserResponse user,
      @Given UUID productId,
      @Given UUID userId) {

    request.setUserId(userId);
    request.setItems(List.of(new OrderItemRequest().productId(productId).quantity(1)));
    when(productService.getProductById(productId)).thenReturn(product);
    when(repo.save(any(OrderEntity.class))).thenAnswer(i -> i.getArgument(0));
    when(userService.getUserById(userId)).thenReturn(user.id(userId));

    OrderResponse result = service.createOrder(request);

    assertThat(result)
        .isNotNull()
        .satisfies(
            r -> {
              assertThat(r.getUser().getUserId()).isEqualTo(userId);
              assertThat(r.getUser().getFirstName()).isEqualTo(user.getFirstName());
              assertThat(r.getUser().getLastName()).isEqualTo(user.getLastName());
              assertThat(r.getUser().getEmail()).isEqualTo(user.getEmail());
              assertThat(r.getItems())
                  .singleElement()
                  .satisfies(
                      item -> {
                        assertThat(item.getProductId()).isEqualTo(productId);
                        assertThat(item.getName()).isEqualTo(product.getName());
                        assertThat(item.getQuantity()).isEqualTo(1);
                        assertThat(item.getPrice()).isEqualTo(product.getPrice());
                      });
              assertThat(r.getTotal())
                  .isEqualTo(
                      r.getItems().stream()
                          .map(
                              item ->
                                  item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                          .reduce(BigDecimal.ZERO, BigDecimal::add)
                          .setScale(2, RoundingMode.HALF_UP));
            });

    verify(productService).getProductById(productId);
    verify(userService).getUserById(userId);
  }

  @Test
  void shouldDeleteOrder(@Given UUID orderId) {
    doNothing().when(repo).deleteById(orderId);
    service.deleteOrder(orderId);
    verify(repo).deleteById(orderId);
  }

  @Test
  void shouldGetOrderById(@Given UUID orderId, @Given OrderEntity entity) {

    when(repo.findById(orderId)).thenReturn(Optional.of(entity));

    OrderResponse result = service.getOrderById(orderId);

    assertThat(result).isNotNull();

    verify(repo).findById(orderId);
  }

  @Test
  void getOrderByIdShouldThrow(@Given UUID orderId) {
    assertThrows(ResourceNotFoundException.class, () -> service.getOrderById(orderId));
  }

  @Test
  void shouldGetOrdersByUserId(@Given UUID userId, @Given OrderEntity entity) {
    when(repo.findByUserId(userId)).thenReturn(List.of(entity));

    List<OrderResponse> result = service.getOrdersByUserId(userId);

    assertThat(result).isNotNull().hasSize(1);

    verify(repo).findByUserId(userId);
  }

  @Test
  void shouldGetOrders(@Given OrderEntity order) {

    when(repo.findAllWithItems()).thenReturn(List.of(order));

    List<OrderResponse> result = service.getOrders();

    assertThat(result).isNotNull().hasSize(1);

    verify(repo).findAllWithItems();
  }

  @Test
  void shouldGetMostSoldProducts(@Given List<MostSoldProductResponse> expected) {

    when(repo.getMostSoldProduct()).thenReturn(expected);

    List<MostSoldProductResponse> result = service.getMostSoldProducts();

    assertThat(result)
        .isNotNull()
        .hasSameSizeAs(expected)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactlyElementsOf(expected);

    verify(repo).getMostSoldProduct();
  }

  @Test
  void shouldGetTotalSpentPerUser(@Given List<TotalSpentPerUserResponse> expected) {

    when(repo.getTotalSpentPerUser()).thenReturn(expected);

    List<TotalSpentPerUserResponse> result = service.getTotalSpentPerUser();

    assertThat(result)
        .isNotNull()
        .hasSameSizeAs(expected)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactlyElementsOf(expected);

    verify(repo).getTotalSpentPerUser();
  }
}
