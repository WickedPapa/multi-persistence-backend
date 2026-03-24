package it.montano.multipersistencebackend.common.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import it.montano.multipersistencebackend.config.ConfiguredTest;
import it.montano.multipersistencebackend.dto.UserRequest;
import it.montano.multipersistencebackend.dto.UserResponse;
import it.montano.multipersistencebackend.user.model.UserDocument;
import it.montano.multipersistencebackend.user.model.UserEntity;
import org.instancio.junit.Given;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

@ConfiguredTest
class UserMapperTest {

  UserMapper mapper = Mappers.getMapper(UserMapper.class);

  @Test
  void shouldMapEntityToResponse(@Given UserEntity entity) {
    UserResponse result = mapper.toResponse(entity);

    assertThat(result).isNotNull().usingRecursiveComparison().isEqualTo(entity);
  }

  @Test
  void shouldMapDocumentToResponse(@Given UserDocument document) {
    UserResponse result = mapper.toResponse(document);

    assertThat(result).isNotNull().usingRecursiveComparison().isEqualTo(document);
  }

  @Test
  void shouldMapToEntity(@Given UserRequest request) {
    UserEntity result = mapper.toEntity(request);

    assertThat(result)
        .isNotNull()
        .usingRecursiveComparison()
        .ignoringFields("id")
        .isEqualTo(request);
  }

  @Test
  void shouldMapToDocument(@Given UserRequest request) {
    UserDocument result = mapper.toDocument(request);

    assertThat(result)
        .isNotNull()
        .satisfies(r -> assertThat(r.getId()).isNotNull())
        .usingRecursiveComparison()
        .ignoringFields("id")
        .isEqualTo(request);
  }
}
