package it.montano.multipersistencebackend.common.mapper;

import it.montano.multipersistencebackend.common.dto.OrderItemRequestDto;
import it.montano.multipersistencebackend.common.dto.OrderRequestDto;
import it.montano.multipersistencebackend.dto.*;
import it.montano.multipersistencebackend.order.model.OrderDocument;
import it.montano.multipersistencebackend.order.model.OrderEntity;
import it.montano.multipersistencebackend.order.model.OrderItemEmbedded;
import it.montano.multipersistencebackend.order.model.OrderItemEntity;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(imports = {UUID.class})
public interface OrderMapper {

  @Mapping(target = "user.userId", source = "user.id")
  @Mapping(target = "user.firstName", source = "userFirstNameSnapshot")
  @Mapping(target = "user.lastName", source = "userLastNameSnapshot")
  @Mapping(target = "user.email", source = "userEmailSnapshot")
  OrderResponse toResponse(OrderEntity entity);

  OrderItemResponse toResponse(OrderItemEntity entity);

  @Mapping(target = "firstName", ignore = true)
  @Mapping(target = "lastName", ignore = true)
  @Mapping(target = "email", ignore = true)
  OrderRequestDto toDto(OrderRequest request);

  @Mapping(target = "name", ignore = true)
  @Mapping(target = "price", ignore = true)
  OrderItemRequestDto toDto(OrderItemRequest request);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "user.id", source = "userId")
  @Mapping(target = "userFirstNameSnapshot", source = "firstName")
  @Mapping(target = "userLastNameSnapshot", source = "lastName")
  @Mapping(target = "userEmailSnapshot", source = "email")
  @Mapping(target = "total", expression = "java(calculateTotal(request.getItems()))")
  OrderEntity toEntity(OrderRequestDto request);

  @AfterMapping
  default void linkItems(@MappingTarget OrderEntity order) {
    order.getItems().forEach(item -> item.setOrder(order));
  }

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "order", ignore = true)
  OrderItemEntity toEntity(OrderItemRequestDto request);

  OrderResponse toResponse(OrderDocument order);

  @Mapping(target = "productId", source = "productEmbedded.productId")
  @Mapping(target = "name", source = "productEmbedded.name")
  @Mapping(target = "price", source = "productEmbedded.price")
  OrderItemResponse toResponse(OrderItemEmbedded orderItem);

  @Mapping(target = "id", expression = "java(UUID.randomUUID())")
  @Mapping(target = "user.userId", source = "userId")
  @Mapping(target = "user.firstName", source = "firstName")
  @Mapping(target = "user.lastName", source = "lastName")
  @Mapping(target = "user.email", source = "email")
  @Mapping(target = "total", expression = "java(calculateTotal(request.getItems()))")
  OrderDocument toDocument(OrderRequestDto request);

  @Mapping(target = "productEmbedded.productId", source = "productId")
  @Mapping(target = "productEmbedded.name", source = "name")
  @Mapping(target = "productEmbedded.price", source = "price")
  OrderItemEmbedded toDocument(OrderItemRequestDto request);

  @Mapping(target = "userId", source = "id")
  OrderUserResponse toOrderUserResponse(UserResponse userResponse);

  default @NonNull BigDecimal calculateTotal(@NonNull List<OrderItemRequestDto> items) {
    return items.stream()
        .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
        .reduce(BigDecimal.ZERO, BigDecimal::add)
        .setScale(2, RoundingMode.HALF_UP);
  }
}
