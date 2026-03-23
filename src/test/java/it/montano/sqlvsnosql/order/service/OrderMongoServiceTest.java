package it.montano.sqlvsnosql.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import it.montano.sqlvsnosql.common.mapper.OrderMapper;
import it.montano.sqlvsnosql.config.ConfiguredTest;
import it.montano.sqlvsnosql.config.exeption.ResourceNotFoundException;
import it.montano.sqlvsnosql.dto.*;
import it.montano.sqlvsnosql.order.model.OrderDocument;
import it.montano.sqlvsnosql.order.repository.OrderMongoRepository;
import it.montano.sqlvsnosql.product.service.ProductService;
import it.montano.sqlvsnosql.user.service.UserService;
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
class OrderMongoServiceTest {

  @InjectMocks OrderMongoService service;

  @Mock UserService userService;
  @Mock ProductService productService;
  @Mock OrderMongoRepository repo;

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
    when(userService.getUserById(userId)).thenReturn(user.id(userId));
    when(repo.save(any(OrderDocument.class))).thenAnswer(i -> i.getArgument(0));

    OrderResponse result = service.createOrder(request);

    assertThat(result)
        .isNotNull()
        .satisfies(
            r -> {
              assertThat(r.getUser().getUserId()).isEqualTo(userId);
              assertThat(r.getItems())
                  .singleElement()
                  .satisfies(
                      item -> {
                        assertThat(item.getProductId()).isEqualTo(productId);
                        assertThat(item.getName()).isEqualTo(product.getName());
                        assertThat(item.getPrice()).isEqualTo(product.getPrice());
                        assertThat(item.getQuantity()).isEqualTo(1);
                      });
              assertThat(r.getTotal())
                  .isEqualTo(
                      r.getItems().stream()
                          .mapToDouble(item -> item.getPrice() * item.getQuantity())
                          .sum());
            });

    verify(productService).getProductById(productId);
    verify(userService).getUserById(userId);
  }

  @Test
  void shouldDeleteOrder(@Given UUID orderId) {
    service.deleteOrder(orderId);
    verify(repo).deleteById(orderId);
  }

  @Test
  void shouldGetOrderById(@Given UUID orderId, @Given OrderDocument document) {

    when(repo.findById(orderId)).thenReturn(Optional.of(document));

    OrderResponse result = service.getOrderById(orderId);

    assertThat(result).isNotNull();

    verify(repo).findById(orderId);
  }

  @Test
  void getOrderByIdShouldThrow(@Given UUID orderId) {
    assertThrows(ResourceNotFoundException.class, () -> service.getOrderById(orderId));
  }

  @Test
  void shouldGetOrdersByUserId(@Given UUID userId, @Given OrderDocument document) {

    when(repo.findByUserUserId(userId)).thenReturn(List.of(document));

    List<OrderResponse> result = service.getOrdersByUserId(userId);

    assertThat(result).isNotNull().hasSize(1);

    verify(repo).findByUserUserId(userId);
  }

  @Test
  void shouldGetOrders(@Given OrderDocument document) {

    when(repo.findAll()).thenReturn(List.of(document));

    List<OrderResponse> result = service.getOrders();

    assertThat(result).isNotNull().hasSize(1);

    verify(repo).findAll();
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
