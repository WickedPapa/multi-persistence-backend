package it.montano.sqlvsnosql.common.mapper;

import it.montano.sqlvsnosql.common.dto.OrderItemRequestDto;
import it.montano.sqlvsnosql.common.dto.OrderRequestDto;
import it.montano.sqlvsnosql.dto.*;
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

  @Mapping(target = "name", ignore = true)
  OrderItemResponse toResponse(OrderItemEntity entity);

  OrderRequestDto toDto(OrderRequest request);

  @Mapping(target = "name", ignore = true)
  @Mapping(target = "unitPrice", ignore = true)
  OrderItemRequestDto toDto(OrderItemRequest request);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "total", expression = "java(calculateTotal(request.getItems()))")
  OrderEntity toEntity(OrderRequestDto request);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "order", ignore = true)
  OrderItemEntity toEntity(OrderItemRequestDto request);

  OrderResponse toResponse(OrderDocument entity);

  @Mapping(target = "productId", source = "productDocument.id")
  @Mapping(target = "name", source = "productDocument.name")
  @Mapping(target = "unitPrice", source = "productDocument.price")
  OrderItemResponse toResponse(OrderItemDocument entity);

  @Mapping(target = "id", expression = "java(UUID.randomUUID())")
  @Mapping(target = "total", expression = "java(calculateTotal(request.getItems()))")
  OrderDocument toDocument(OrderRequestDto request);

  @Mapping(target = "productDocument.id", source = "productId")
  @Mapping(target = "productDocument.name", source = "name")
  @Mapping(target = "productDocument.price", source = "unitPrice")
  OrderItemDocument toDocument(OrderItemRequestDto request);

  default @NonNull Double calculateTotal(@NonNull List<OrderItemRequestDto> items) {
    return items.stream().mapToDouble(item -> item.getUnitPrice() * item.getQuantity()).sum();
  }
}
