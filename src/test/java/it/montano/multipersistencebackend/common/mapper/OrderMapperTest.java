package it.montano.multipersistencebackend.common.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import it.montano.multipersistencebackend.common.dto.OrderItemRequestDto;
import it.montano.multipersistencebackend.common.dto.OrderRequestDto;
import it.montano.multipersistencebackend.config.ConfiguredTest;
import it.montano.multipersistencebackend.dto.*;
import it.montano.multipersistencebackend.order.model.OrderDocument;
import it.montano.multipersistencebackend.order.model.OrderEntity;
import java.util.List;
import org.instancio.junit.Given;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

@ConfiguredTest
class OrderMapperTest {

  OrderMapper mapper = Mappers.getMapper(OrderMapper.class);

  @Test
  void shouldMapRequestToDto(
      @Given OrderRequest orderRequest, @Given OrderItemRequest orderItemRequest) {
    OrderRequestDto dto = mapper.toDto(orderRequest);

    assertThat(dto)
        .isNotNull()
        .usingRecursiveComparison()
        .ignoringFields("items.price", "items.name")
        .isEqualTo(orderRequest);
  }

  @Test
  void shouldMapDtoToEntityAndLinkItems(@Given OrderRequestDto requestDto) {
    OrderEntity entity = mapper.toEntity(requestDto);

    assertThat(entity)
        .isNotNull()
        .usingRecursiveComparison()
        .ignoringFields("id", "total", "items.id", "items.order")
        .isEqualTo(requestDto);
  }

  @Test
  void shouldLinkItems(@Given OrderEntity order) {
    order.getItems().forEach(item -> item.setOrder(null));
    mapper.linkItems(order);
    assertThat(order.getItems())
        .allSatisfy(
            item -> {
              assertThat(item.getOrder()).isNotNull();
              assertThat(item.getOrder()).isSameAs(order);
            });
  }

  @Test
  void shouldMapEntityToResponse(@Given OrderEntity entity) {
    OrderResponse response = mapper.toResponse(entity);

    assertThat(response)
        .isNotNull()
        .satisfies(r -> assertThat(r.getUser().getUserId()).isEqualTo(entity.getUserId()))
        .usingRecursiveComparison()
        .ignoringFields("user", "items.name")
        .isEqualTo(entity);
  }

  @Test
  void shouldMapDocumentToResponse(@Given OrderDocument document) {
    OrderResponse response = mapper.toResponse(document);

    assertThat(response)
        .isNotNull()
        .satisfies(
            res -> {
              assertThat(res.getItems())
                  .zipSatisfy(
                      document.getItems(),
                      (r, d) -> {
                        assertThat(r.getProductId()).isEqualTo(d.getProductEmbedded().getId());
                        assertThat(r.getName()).isEqualTo(d.getProductEmbedded().getName());
                        assertThat(r.getPrice()).isEqualTo(d.getProductEmbedded().getPrice());
                      });
            })
        .usingRecursiveComparison()
        .ignoringFields("items.productId", "items.name", "items.price")
        .isEqualTo(document);
  }

  @Test
  void shouldMapToDocument(@Given OrderRequestDto request, @Given UserResponse userResponse) {
    OrderDocument document = mapper.toDocument(request, userResponse);

    assertThat(document)
        .isNotNull()
        .satisfies(
            res -> {
              assertThat(res.getItems())
                  .zipSatisfy(
                      request.getItems(),
                      (r, d) -> {
                        assertThat(r.getProductEmbedded().getId()).isEqualTo(d.getProductId());
                        assertThat(r.getProductEmbedded().getName()).isEqualTo(d.getName());
                        assertThat(r.getProductEmbedded().getPrice()).isEqualTo(d.getPrice());
                      });
              assertThat(res.getId()).isNotNull();
              assertThat(res.getUser().getUserId()).isEqualTo(userResponse.getId());
              assertThat(res.getUser().getFirstName()).isEqualTo(userResponse.getFirstName());
              assertThat(res.getUser().getLastName()).isEqualTo(userResponse.getLastName());
              assertThat(res.getUser().getEmail()).isEqualTo(userResponse.getEmail());
              assertThat(res.getTotal()).isEqualTo(mapper.calculateTotal(request.getItems()));
            });
  }

  @Test
  void shouldMapToOrderUserResponse(@Given UserResponse userResponse) {
    OrderUserResponse response = mapper.toOrderUserResponse(userResponse);

    assertThat(response)
        .isNotNull()
        .satisfies(r -> assertThat(r.getUserId()).isEqualTo(userResponse.getId()))
        .usingRecursiveComparison()
        .ignoringFields("userId")
        .isEqualTo(userResponse);
  }

  @Test
  void shouldCalculateTotal() {
    OrderItemRequestDto item1 = new OrderItemRequestDto();
    OrderItemRequestDto item2 = new OrderItemRequestDto();
    item1.setPrice(5.0);
    item1.setQuantity(1);
    item2.setPrice(2.5);
    item2.setQuantity(2);
    Double total = mapper.calculateTotal(List.of(item1, item2));
    assertThat(total).isNotNull().isEqualTo(10.0);
  }
}
