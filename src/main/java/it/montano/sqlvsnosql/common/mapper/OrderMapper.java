package it.montano.sqlvsnosql.common.mapper;

import it.montano.sqlvsnosql.common.dto.OrderItemRequestDto;
import it.montano.sqlvsnosql.common.dto.OrderRequestDto;
import it.montano.sqlvsnosql.dto.OrderItemRequest;
import it.montano.sqlvsnosql.dto.OrderItemResponse;
import it.montano.sqlvsnosql.dto.OrderRequest;
import it.montano.sqlvsnosql.dto.OrderResponse;
import it.montano.sqlvsnosql.order.model.OrderDocument;
import it.montano.sqlvsnosql.order.model.OrderEntity;
import it.montano.sqlvsnosql.order.model.OrderItemDocument;
import it.montano.sqlvsnosql.order.model.OrderItemEntity;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(imports = {UUID.class})
public interface OrderMapper {

  OrderResponse toResponse(OrderEntity entity);

  @Mapping(target = "itemId", source = "id")
  OrderItemResponse toResponse(OrderItemEntity entity);

  OrderRequestDto toDto(OrderRequest request);

  @Mapping(target = "unitPrice", ignore = true)
  OrderItemRequestDto toDto(OrderItemRequest request);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "total", expression = "java(calculateTotal(request.getItems()))")
  OrderEntity toEntity(OrderRequestDto request);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "order", ignore = true)
  OrderItemEntity toEntity(OrderItemRequestDto request);

  OrderResponse toResponse(OrderDocument entity);

  @Mapping(target = "itemId", source = "id")
  OrderItemResponse toResponse(OrderItemDocument entity);

  @Mapping(target = "id", expression = "java(UUID.randomUUID())")
  @Mapping(target = "total", expression = "java(calculateTotal(request.getItems()))")
  OrderDocument toDocument(OrderRequestDto request);

  @Mapping(target = "id", expression = "java(UUID.randomUUID())")
  OrderItemDocument toDocument(OrderItemRequestDto request);

  default @NonNull Double calculateTotal(@NonNull List<OrderItemRequestDto> items) {
    return items.stream().mapToDouble(OrderItemRequestDto::getUnitPrice).sum();
  }
}
