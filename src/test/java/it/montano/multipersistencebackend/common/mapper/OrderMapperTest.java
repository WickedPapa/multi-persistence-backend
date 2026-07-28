package it.montano.multipersistencebackend.common.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import it.montano.multipersistencebackend.common.dto.OrderItemRequestDto;
import it.montano.multipersistencebackend.common.dto.OrderRequestDto;
import it.montano.multipersistencebackend.config.ConfiguredTest;
import it.montano.multipersistencebackend.dto.*;
import it.montano.multipersistencebackend.order.model.OrderDocument;
import it.montano.multipersistencebackend.order.model.OrderEntity;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.instancio.junit.Given;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

@ConfiguredTest
class OrderMapperTest {

  OrderMapper mapper = Mappers.getMapper(OrderMapper.class);

  @Test
  void shouldMapRequestToDto(@Given OrderRequest orderRequest) {
    OrderRequestDto dto = mapper.toDto(orderRequest);

    assertThat(dto)
        .isNotNull()
        .usingRecursiveComparison()
        .ignoringFields("firstName", "lastName", "email", "items.price", "items.name")
        .isEqualTo(orderRequest);
  }

  @Test
  void shouldMapDtoToEntityAndLinkItems(@Given OrderRequestDto requestDto) {
    OrderEntity entity = mapper.toEntity(requestDto);

    assertThat(entity)
        .isNotNull()
        .satisfies(
            e -> {
              assertThat(e.getId()).isNull();
              assertThat(e.getUser()).isNotNull();
              assertThat(e.getUser().getId()).isEqualTo(requestDto.getUserId());
              assertThat(e.getUserFirstNameSnapshot()).isEqualTo(requestDto.getFirstName());
              assertThat(e.getUserLastNameSnapshot()).isEqualTo(requestDto.getLastName());
              assertThat(e.getUserEmailSnapshot()).isEqualTo(requestDto.getEmail());
              assertThat(e.getTotal()).isEqualTo(mapper.calculateTotal(requestDto.getItems()));
              assertThat(e.getItems())
                  .zipSatisfy(
                      requestDto.getItems(),
                      (item, source) -> {
                        assertThat(item.getId()).isNull();
                        assertThat(item.getOrder()).isSameAs(e);
                        assertThat(item.getProductId()).isEqualTo(source.getProductId());
                        assertThat(item.getName()).isEqualTo(source.getName());
                        assertThat(item.getPrice()).isEqualTo(source.getPrice());
                        assertThat(item.getQuantity()).isEqualTo(source.getQuantity());
                      });
            });
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
        .satisfies(
            r -> {
              assertThat(r.getUser().getUserId()).isEqualTo(entity.getUser().getId());
              assertThat(r.getUser().getFirstName()).isEqualTo(entity.getUserFirstNameSnapshot());
              assertThat(r.getUser().getLastName()).isEqualTo(entity.getUserLastNameSnapshot());
              assertThat(r.getUser().getEmail()).isEqualTo(entity.getUserEmailSnapshot());
              assertThat(r.getItems())
                  .zipSatisfy(
                      entity.getItems(),
                      (itemResponse, itemEntity) -> {
                        assertThat(itemResponse.getProductId())
                            .isEqualTo(itemEntity.getProductId());
                        assertThat(itemResponse.getName()).isEqualTo(itemEntity.getName());
                        assertThat(itemResponse.getPrice()).isEqualTo(itemEntity.getPrice());
                      });
            })
        .satisfies(r -> assertThat(r.getTotal()).isEqualTo(entity.getTotal()));
  }

  @Test
  void shouldMapDocumentToResponse(@Given OrderDocument document) {
    OrderResponse response = mapper.toResponse(document);

    assertThat(response)
        .isNotNull()
        .satisfies(
            res ->
                assertThat(res.getItems())
                    .zipSatisfy(
                        document.getItems(),
                        (r, d) -> {
                          assertThat(r.getProductId())
                              .isEqualTo(d.getProductEmbedded().getProductId());
                          assertThat(r.getName()).isEqualTo(d.getProductEmbedded().getName());
                          assertThat(r.getPrice()).isEqualTo(d.getProductEmbedded().getPrice());
                        }))
        .usingRecursiveComparison()
        .ignoringFields("items.productId", "items.name", "items.price")
        .isEqualTo(document);
  }

  @Test
  void shouldMapToDocument(@Given OrderRequestDto request) {
    OrderDocument document = mapper.toDocument(request);

    assertThat(document)
        .isNotNull()
        .satisfies(
            res -> {
              assertThat(res.getItems())
                  .zipSatisfy(
                      request.getItems(),
                      (r, d) -> {
                        assertThat(r.getProductEmbedded().getProductId())
                            .isEqualTo(d.getProductId());
                        assertThat(r.getProductEmbedded().getName()).isEqualTo(d.getName());
                        assertThat(r.getProductEmbedded().getPrice()).isEqualTo(d.getPrice());
                      });
              assertThat(res.getId()).isNotNull();
              assertThat(res.getUser().getUserId()).isEqualTo(request.getUserId());
              assertThat(res.getUser().getFirstName()).isEqualTo(request.getFirstName());
              assertThat(res.getUser().getLastName()).isEqualTo(request.getLastName());
              assertThat(res.getUser().getEmail()).isEqualTo(request.getEmail());
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
    item1.setPrice(BigDecimal.valueOf(5));
    item1.setQuantity(1);
    item2.setPrice(BigDecimal.valueOf(2.5));
    item2.setQuantity(2);
    BigDecimal total = mapper.calculateTotal(List.of(item1, item2));
    assertThat(total)
        .isNotNull()
        .isEqualTo(BigDecimal.valueOf(10).setScale(2, RoundingMode.HALF_UP));
  }
}
