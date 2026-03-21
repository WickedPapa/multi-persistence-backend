package it.montano.sqlvsnosql.common.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import it.montano.sqlvsnosql.config.ConfiguredTest;
import it.montano.sqlvsnosql.dto.ProductRequest;
import it.montano.sqlvsnosql.dto.ProductResponse;
import it.montano.sqlvsnosql.product.model.ProductDocument;
import it.montano.sqlvsnosql.product.model.ProductEntity;
import org.instancio.junit.Given;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

@ConfiguredTest
class ProductMapperTest {

  ProductMapper mapper = Mappers.getMapper(ProductMapper.class);

  @Test
  void shouldMapEntityToResponse(@Given ProductEntity entity) {
    ProductResponse result = mapper.toResponse(entity);

    assertThat(result).isNotNull().usingRecursiveComparison().isEqualTo(entity);
  }

  @Test
  void shouldMapDocumentToResponse(@Given ProductDocument document) {
    ProductResponse result = mapper.toResponse(document);

    assertThat(result).isNotNull().usingRecursiveComparison().isEqualTo(document);
  }

  @Test
  void shouldMapToEntity(@Given ProductRequest request) {
    ProductEntity result = mapper.toEntity(request);

    assertThat(result)
        .isNotNull()
        .satisfies(r -> assertThat(r.getId()).isNull())
        .usingRecursiveComparison()
        .ignoringFields("id")
        .isEqualTo(request);
  }

  @Test
  void shouldMapToDocument(@Given ProductRequest request) {
    ProductDocument result = mapper.toDocument(request);

    assertThat(result)
        .isNotNull()
        .satisfies(r -> assertThat(r.getId()).isNotNull())
        .usingRecursiveComparison()
        .ignoringFields("id")
        .isEqualTo(request);
  }
}
