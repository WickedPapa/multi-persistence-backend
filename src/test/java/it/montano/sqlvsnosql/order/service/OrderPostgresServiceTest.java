package it.montano.sqlvsnosql.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import it.montano.sqlvsnosql.common.mapper.OrderMapper;
import it.montano.sqlvsnosql.config.ConfiguredTest;
import it.montano.sqlvsnosql.config.exeption.ResourceNotFoundException;
import it.montano.sqlvsnosql.dto.*;
import it.montano.sqlvsnosql.order.model.OrderEntity;
import it.montano.sqlvsnosql.order.model.OrderItemEntity;
import it.montano.sqlvsnosql.order.repository.OrderPostgresRepository;
import it.montano.sqlvsnosql.product.service.ProductService;
import it.montano.sqlvsnosql.user.service.UserService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.instancio.Instancio;
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
                          .mapToDouble(item -> item.getPrice() * item.getQuantity())
                          .sum());
            });

    verify(productService, times(2)).getProductById(productId);
    verify(userService).getUserById(userId);
  }

  @Test
  void shouldDeleteOrder(@Given UUID orderId) {
    doNothing().when(repo).deleteById(orderId);
    service.deleteOrder(orderId);
    verify(repo).deleteById(orderId);
  }

  @Test
  void shouldGetOrderById(
      @Given UUID orderId, @Given ProductResponse product, @Given UserResponse user) {

    OrderItemEntity orderItemEntity = Instancio.create(OrderItemEntity.class);
    OrderEntity entity =
        Instancio.of(OrderEntity.class)
            .set(field(OrderEntity::getItems), List.of(orderItemEntity))
            .create();
    when(repo.findById(orderId)).thenReturn(Optional.of(entity));
    when(productService.getProductById(any())).thenReturn(product);
    when(userService.getUserById(any())).thenReturn(user);

    OrderResponse result = service.getOrderById(orderId);

    assertThat(result)
        .isNotNull()
        .satisfies(
            r -> {
              assertThat(r.getUser().getUserId()).isEqualTo(user.getId());
              assertThat(r.getUser().getFirstName()).isEqualTo(user.getFirstName());
              assertThat(r.getUser().getLastName()).isEqualTo(user.getLastName());
              assertThat(r.getUser().getEmail()).isEqualTo(user.getEmail());
              assertThat(r.getItems())
                  .singleElement()
                  .satisfies(
                      item -> {
                        assertThat(item.getProductId()).isEqualTo(orderItemEntity.getProductId());
                        assertThat(item.getName()).isEqualTo(product.getName());
                        assertThat(item.getQuantity()).isEqualTo(orderItemEntity.getQuantity());
                        assertThat(item.getPrice()).isEqualTo(orderItemEntity.getPrice());
                      });
            });

    verify(repo).findById(orderId);
    verify(userService).getUserById(any());
  }

  @Test
  void getOrderByIdShouldThrow(@Given UUID orderId) {
    assertThrows(ResourceNotFoundException.class, () -> service.getOrderById(orderId));
  }

  @Test
  void shouldGetOrdersByUserId(
      @Given UUID userId, @Given ProductResponse product, @Given UserResponse user) {

    OrderItemEntity orderItemEntity = Instancio.create(OrderItemEntity.class);
    OrderEntity entity =
        Instancio.of(OrderEntity.class)
            .set(field(OrderEntity::getItems), List.of(orderItemEntity))
            .create();
    when(repo.findByUserId(userId)).thenReturn(List.of(entity));
    when(productService.getProductById(any())).thenReturn(product);
    when(userService.getUserById(any())).thenReturn(user);

    List<OrderResponse> result = service.getOrdersByUserId(userId);

    assertThat(result)
        .isNotNull()
        .singleElement()
        .satisfies(
            r -> {
              assertThat(r.getUser().getUserId()).isEqualTo(user.getId());
              assertThat(r.getUser().getFirstName()).isEqualTo(user.getFirstName());
              assertThat(r.getUser().getLastName()).isEqualTo(user.getLastName());
              assertThat(r.getUser().getEmail()).isEqualTo(user.getEmail());
              assertThat(r.getItems())
                  .singleElement()
                  .satisfies(
                      item -> {
                        assertThat(item.getProductId()).isEqualTo(orderItemEntity.getProductId());
                        assertThat(item.getName()).isEqualTo(product.getName());
                        assertThat(item.getQuantity()).isEqualTo(orderItemEntity.getQuantity());
                        assertThat(item.getPrice()).isEqualTo(orderItemEntity.getPrice());
                      });
            });

    verify(repo).findByUserId(userId);
    verify(userService).getUserById(any());
  }

  @Test
  void shouldGetOrders(
      @Given UUID userId, @Given ProductResponse product, @Given UserResponse user) {

    OrderItemEntity orderItemEntity = Instancio.create(OrderItemEntity.class);

    OrderEntity entity =
        Instancio.of(OrderEntity.class)
            .set(field(OrderEntity::getUserId), userId)
            .set(field(OrderEntity::getItems), List.of(orderItemEntity))
            .create();

    when(repo.findAll()).thenReturn(List.of(entity));
    when(productService.getProductById(orderItemEntity.getProductId())).thenReturn(product);
    when(userService.getUserById(userId)).thenReturn(user);

    List<OrderResponse> result = service.getOrders();

    assertThat(result)
        .isNotNull()
        .singleElement()
        .satisfies(
            r -> {
              assertThat(r.getUser().getUserId()).isEqualTo(user.getId());
              assertThat(r.getUser().getFirstName()).isEqualTo(user.getFirstName());
              assertThat(r.getUser().getLastName()).isEqualTo(user.getLastName());
              assertThat(r.getUser().getEmail()).isEqualTo(user.getEmail());
              assertThat(r.getItems())
                  .singleElement()
                  .satisfies(
                      item -> {
                        assertThat(item.getProductId()).isEqualTo(orderItemEntity.getProductId());
                        assertThat(item.getName()).isEqualTo(product.getName());
                        assertThat(item.getQuantity()).isEqualTo(orderItemEntity.getQuantity());
                        assertThat(item.getPrice()).isEqualTo(orderItemEntity.getPrice());
                      });
            });

    verify(repo).findAll();
    verify(userService).getUserById(userId);
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
