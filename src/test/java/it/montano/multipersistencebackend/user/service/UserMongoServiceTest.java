package it.montano.multipersistencebackend.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import it.montano.multipersistencebackend.common.mapper.UserMapper;
import it.montano.multipersistencebackend.config.ConfiguredTest;
import it.montano.multipersistencebackend.config.exeption.ResourceNotFoundException;
import it.montano.multipersistencebackend.dto.UserRequest;
import it.montano.multipersistencebackend.dto.UserResponse;
import it.montano.multipersistencebackend.user.model.UserDocument;
import it.montano.multipersistencebackend.user.repository.UserMongoRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.instancio.junit.Given;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

@ConfiguredTest
class UserMongoServiceTest {

  @InjectMocks UserMongoService service;

  @Mock UserMapper mapper;
  @Mock UserMongoRepository repo;

  @Test
  void shouldCreateUser(
      @Given UserRequest request, @Given UserDocument document, @Given UserResponse response) {

    when(mapper.toDocument(request)).thenReturn(document);
    when(repo.save(document)).thenReturn(document);
    when(mapper.toResponse(document)).thenReturn(response);

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
      @Given UUID userId, @Given UserDocument document, @Given UserResponse response) {

    when(repo.findById(userId)).thenReturn(Optional.of(document));
    when(mapper.toResponse(document)).thenReturn(response);

    UserResponse result = service.getUserById(userId);

    assertThat(result).isNotNull().isEqualTo(response);
  }

  @Test
  void getUserByIdShouldThrow(@Given UUID userId) {
    assertThrows(ResourceNotFoundException.class, () -> service.getUserById(userId));
  }

  @Test
  void shouldGetUsers(@Given List<UserDocument> documents, @Given UserResponse response) {

    when(repo.findAll()).thenReturn(documents);
    when(mapper.toResponse(any(UserDocument.class))).thenReturn(response);

    List<UserResponse> result = service.getUsers();

    assertThat(result).isNotNull().contains(response);
  }
}
