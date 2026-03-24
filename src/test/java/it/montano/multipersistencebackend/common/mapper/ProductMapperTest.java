package it.montano.multipersistencebackend.common.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import it.montano.multipersistencebackend.config.ConfiguredTest;
import it.montano.multipersistencebackend.dto.ProductRequest;
import it.montano.multipersistencebackend.dto.ProductResponse;
import it.montano.multipersistencebackend.product.model.ProductDocument;
import it.montano.multipersistencebackend.product.model.ProductEntity;
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
        .satisfies(
            r -> {
              assertThat(r.getId()).isNull();
              assertThat(r.getPrice()).isEqualTo(mapper.roundPrice(request.getPrice()));
            })
        .usingRecursiveComparison()
        .ignoringFields("id", "price")
        .isEqualTo(request);
  }

  @Test
  void shouldMapToDocument(@Given ProductRequest request) {
    ProductDocument result = mapper.toDocument(request);

    assertThat(result)
        .isNotNull()
        .satisfies(
            r -> {
              assertThat(r.getId()).isNotNull();
              assertThat(r.getPrice()).isEqualTo(mapper.roundPrice(request.getPrice()));
            })
        .usingRecursiveComparison()
        .ignoringFields("id", "price")
        .isEqualTo(request);
  }

  @Test
  void shouldUpdateEntity(@Given ProductRequest request, @Given ProductEntity entity) {
    mapper.updateEntity(request, entity);

    assertThat(entity)
        .isNotNull()
        .satisfies(r -> assertThat(r.getId()).isNotNull())
        .usingRecursiveComparison()
        .ignoringFields("id")
        .isEqualTo(request);
  }

  @Test
  void shouldUpdateDocument(@Given ProductRequest request, @Given ProductDocument doc) {
    mapper.updateDocument(request, doc);

    assertThat(doc)
        .isNotNull()
        .satisfies(r -> assertThat(r.getId()).isNotNull())
        .usingRecursiveComparison()
        .ignoringFields("id")
        .isEqualTo(request);
  }

  @Test
  void shouldRoundPrice() {
    double initialPrice = 142.412454525;

    double rounded = mapper.roundPrice(initialPrice);

    assertThat(rounded).isEqualTo(142.41);
  }
}
