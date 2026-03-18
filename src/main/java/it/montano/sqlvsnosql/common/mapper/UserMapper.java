package it.montano.sqlvsnosql.common.mapper;

import it.montano.sqlvsnosql.dto.UserRequest;
import it.montano.sqlvsnosql.dto.UserResponse;
import it.montano.sqlvsnosql.user.model.UserDocument;
import it.montano.sqlvsnosql.user.model.UserEntity;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(imports = {UUID.class})
public interface UserMapper {

  UserResponse toResponse(UserEntity entity);

  UserResponse toResponse(UserDocument document);

  @Mapping(target = "id", ignore = true)
  UserEntity toEntity(UserRequest request);

  @Mapping(target = "id", expression = "java(UUID.randomUUID())")
  UserDocument toDocument(UserRequest request);
}
