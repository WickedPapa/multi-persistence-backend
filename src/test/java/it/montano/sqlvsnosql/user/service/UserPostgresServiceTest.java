package it.montano.sqlvsnosql.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import it.montano.sqlvsnosql.common.mapper.UserMapper;
import it.montano.sqlvsnosql.config.ConfiguredTest;
import it.montano.sqlvsnosql.config.exeption.ResourceNotFoundException;
import it.montano.sqlvsnosql.dto.UserRequest;
import it.montano.sqlvsnosql.dto.UserResponse;
import it.montano.sqlvsnosql.user.model.UserEntity;
import it.montano.sqlvsnosql.user.repository.UserPostgresRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.instancio.junit.Given;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

@ConfiguredTest
class UserPostgresServiceTest {

  @InjectMocks UserPostgresService service;

  @Mock UserMapper mapper;
  @Mock UserPostgresRepository repo;

  @Test
  void shouldCreateUser(
      @Given UserRequest request, @Given UserEntity entity, @Given UserResponse response) {
    when(mapper.toEntity(request)).thenReturn(entity);
    when(repo.save(entity)).thenReturn(entity);
    when(mapper.toResponse(entity)).thenReturn(response);

    UserResponse result = service.createUser(request);

    assertThat(result).isNotNull().isEqualTo(response);
  }

  @Test
  void shouldDeleteUser(@Given UUID userId) {
    doNothing().when(repo).deleteById(userId);

    service.deleteUser(userId);

    verify(repo).deleteById(userId);
  }

  @Test
  void shouldGetUserById(
      @Given UUID userId, @Given UserEntity entity, @Given UserResponse response) {
    when(repo.findById(userId)).thenReturn(Optional.of(entity));
    when(mapper.toResponse(entity)).thenReturn(response);

    UserResponse result = service.getUserById(userId);

    assertThat(result).isNotNull().isEqualTo(response);
  }

  @Test
  void getUserByIdShouldThrow(@Given UUID userId) {
    assertThrows(ResourceNotFoundException.class, () -> service.getUserById(userId));
  }

  @Test
  void shouldGetUsers(@Given List<UserEntity> entities, @Given UserResponse response) {
    when(repo.findAll()).thenReturn(entities);
    when(mapper.toResponse(any(UserEntity.class))).thenReturn(response);

    List<UserResponse> result = service.getUsers();

    assertThat(result).isNotNull().contains(response);
  }
}
