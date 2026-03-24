package it.montano.multipersistencebackend.user.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import it.montano.multipersistencebackend.api.UsersApi;
import it.montano.multipersistencebackend.common.util.UriUtil;
import it.montano.multipersistencebackend.config.ConfiguredTest;
import it.montano.multipersistencebackend.dto.UserRequest;
import it.montano.multipersistencebackend.dto.UserResponse;
import it.montano.multipersistencebackend.user.service.UserService;
import java.util.List;
import java.util.UUID;
import org.instancio.junit.Given;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ConfiguredTest
class UserControllerTest {

  @Mock UserService userService;

  UserController controller;

  @BeforeEach
  void setUp() {
    controller = new UserController(userService);
  }

  @Test
  void shouldCreateUser(@Given UserRequest userRequest, @Given UserResponse userResponse) {
    when(userService.createUser(userRequest)).thenReturn(userResponse);

    ResponseEntity<UserResponse> result = controller.createUser(userRequest);

    assertThat(result)
        .isNotNull()
        .satisfies(r -> assertThat(r.getStatusCode()).isEqualTo(HttpStatus.CREATED))
        .satisfies(
            r ->
                assertThat(r.getHeaders().getLocation())
                    .hasToString(
                        UriUtil.buildUri(UsersApi.PATH_GET_USER_BY_ID, userResponse.getId())
                            .toString()))
        .extracting(ResponseEntity::getBody)
        .isEqualTo(userResponse);
  }

  @Test
  void shouldDeleteUser(@Given UUID userId) {
    ResponseEntity<Void> result = controller.deleteUser(userId);

    assertThat(result)
        .isNotNull()
        .extracting(ResponseEntity::getStatusCode)
        .isEqualTo(HttpStatus.NO_CONTENT);
    verify(userService).deleteUser(userId);
  }

  @Test
  void shouldGetUserById(@Given UUID userId, @Given UserResponse userResponse) {
    when(userService.getUserById(userId)).thenReturn(userResponse);

    ResponseEntity<UserResponse> result = controller.getUserById(userId);

    assertThat(result)
        .isNotNull()
        .satisfies(r -> assertThat(r.getStatusCode()).isEqualTo(HttpStatus.OK))
        .extracting(ResponseEntity::getBody)
        .isEqualTo(userResponse);
  }

  @Test
  void shouldGetUsers(@Given List<UserResponse> users) {
    when(userService.getUsers()).thenReturn(users);

    ResponseEntity<List<UserResponse>> result = controller.getUsers();

    assertThat(result)
        .isNotNull()
        .satisfies(r -> assertThat(r.getStatusCode()).isEqualTo(HttpStatus.OK))
        .extracting(ResponseEntity::getBody)
        .isEqualTo(users);
  }
}
