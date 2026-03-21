package it.montano.sqlvsnosql.common.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import it.montano.sqlvsnosql.common.dto.OrderItemRequestDto;
import it.montano.sqlvsnosql.common.dto.OrderRequestDto;
import it.montano.sqlvsnosql.config.ConfiguredTest;
import it.montano.sqlvsnosql.dto.OrderItemRequest;
import it.montano.sqlvsnosql.dto.OrderRequest;
import it.montano.sqlvsnosql.dto.OrderResponse;
import it.montano.sqlvsnosql.dto.OrderUserResponse;
import it.montano.sqlvsnosql.dto.UserResponse;
import it.montano.sqlvsnosql.order.model.*;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

@ConfiguredTest
class OrderMapperTest {

  OrderMapper mapper = Mappers.getMapper(OrderMapper.class);

  @Test
  void shouldMapRequestToDto() {
    UUID userId = UUID.randomUUID();
    OrderItemRequest itemRequest = new OrderItemRequest().productId(UUID.randomUUID()).quantity(3);
    OrderRequestDto dto =
        mapper.toDto(new OrderRequest().userId(userId).items(List.of(itemRequest)));

    assertThat(dto.getUserId()).isEqualTo(userId);
    assertThat(dto.getItems())
        .singleElement()
        .satisfies(
            itemDto -> {
              assertThat(itemDto.getProductId()).isEqualTo(itemRequest.getProductId());
              assertThat(itemDto.getQuantity()).isEqualTo(itemRequest.getQuantity());
            });
  }

  @Test
  void shouldMapDtoToEntityAndLinkItems() {
    UUID userId = UUID.randomUUID();
    OrderItemRequestDto firstItem =
        new OrderItemRequestDto(UUID.randomUUID(), "Phone", 2, 10.0);
    OrderItemRequestDto secondItem =
        new OrderItemRequestDto(UUID.randomUUID(), "Mouse", 1, 5.0);
    OrderRequestDto requestDto = new OrderRequestDto(userId, List.of(firstItem, secondItem));

    OrderEntity entity = mapper.toEntity(requestDto);

    assertThat(entity.getUserId()).isEqualTo(userId);
    assertThat(entity.getTotal()).isEqualTo(25.0);
    assertThat(entity.getItems()).hasSize(2).allSatisfy(item -> assertThat(item.getOrder()).isSameAs(entity));
  }

  @Test
  void shouldMapEntityToResponse() {
    UUID orderId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    UUID productId = UUID.randomUUID();

    OrderItemEntity item = new OrderItemEntity();
    item.setProductId(productId);
    item.setQuantity(4);
    item.setPrice(3.0);

    OrderEntity entity = new OrderEntity();
    entity.setId(orderId);
    entity.setUserId(userId);
    entity.setItems(List.of(item));
    item.setOrder(entity);
    entity.setTotal(12.0);

    OrderResponse response = mapper.toResponse(entity);

    assertThat(response.getId()).isEqualTo(orderId);
    assertThat(response.getTotal()).isEqualTo(12.0);
    assertThat(response.getUser()).extracting(OrderUserResponse::getUserId).isEqualTo(userId);
    assertThat(response.getItems()).singleElement().extracting("productId").isEqualTo(productId);
  }

  @Test
  void shouldMapDocumentToResponse() {
    UUID orderId = UUID.randomUUID();
    UUID productId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    OrderItemDocument itemDocument =
        new OrderItemDocument(new ProductEmbedded(productId, "Laptop", 999.0), 1);
    OrderDocument document =
        new OrderDocument(
            orderId,
            new UserEmbedded(userId, "Ada", "Lovelace", "ada@example.com"),
            List.of(itemDocument),
            999.0);

    OrderResponse response = mapper.toResponse(document);

    assertThat(response.getId()).isEqualTo(orderId);
    assertThat(response.getUser().getFirstName()).isEqualTo("Ada");
    assertThat(response.getItems())
        .singleElement()
        .satisfies(
            item -> {
              assertThat(item.getProductId()).isEqualTo(productId);
              assertThat(item.getName()).isEqualTo("Laptop");
              assertThat(item.getPrice()).isEqualTo(999.0);
            });
  }

  @Test
  void shouldMapToDocument() {
    UUID userId = UUID.randomUUID();
    OrderItemRequestDto itemDto =
        new OrderItemRequestDto(UUID.randomUUID(), "Keyboard", 2, 50.0);
    OrderRequestDto requestDto = new OrderRequestDto(userId, List.of(itemDto));
    UserResponse userResponse =
        new UserResponse().id(userId).firstName("Grace").lastName("Hopper").email("grace@example.com");

    OrderDocument document = mapper.toDocument(requestDto, userResponse);

    assertThat(document.getId()).isNotNull();
    assertThat(document.getUser().getEmail()).isEqualTo("grace@example.com");
    assertThat(document.getItems())
        .singleElement()
        .satisfies(
            item -> {
              assertThat(item.getProductEmbedded().getName()).isEqualTo("Keyboard");
              assertThat(item.getProductEmbedded().getPrice()).isEqualTo(50.0);
            });
    assertThat(document.getTotal()).isEqualTo(100.0);
  }
}
