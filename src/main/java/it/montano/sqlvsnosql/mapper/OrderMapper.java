package it.montano.sqlvsnosql.mapper;

import it.montano.sqlvsnosql.dto.OrderItemRequest;
import it.montano.sqlvsnosql.dto.OrderRequest;
import it.montano.sqlvsnosql.dto.OrderResponse;
import it.montano.sqlvsnosql.entity.OrderEntity;
import it.montano.sqlvsnosql.entity.OrderItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface OrderMapper {

  OrderResponse toResponse(OrderEntity entity);

  @Mapping(target = "id", ignore = true)
  OrderEntity toEntity(OrderRequest request);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "order", ignore = true)
  OrderItemEntity toEntity(OrderItemRequest request);
}
