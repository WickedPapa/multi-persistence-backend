package it.montano.sqlvsnosql.common.mapper;

import it.montano.sqlvsnosql.dto.*;
import it.montano.sqlvsnosql.product.model.ProductDocument;
import it.montano.sqlvsnosql.product.model.ProductEntity;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(imports = {UUID.class})
public interface ProductMapper {

  ProductResponse toResponse(ProductEntity entity);

  ProductResponse toResponse(ProductDocument document);

  @Mapping(target = "id", ignore = true)
  ProductEntity toEntity(ProductRequest request);

  @Mapping(target = "id", expression = "java(UUID.randomUUID())")
  ProductDocument toDocument(ProductRequest request);
}
