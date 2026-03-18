package it.montano.sqlvsnosql.user.controller;

import it.montano.sqlvsnosql.api.UsersApi;
import it.montano.sqlvsnosql.common.util.UriUtil;
import it.montano.sqlvsnosql.dto.UserRequest;
import it.montano.sqlvsnosql.dto.UserResponse;
import it.montano.sqlvsnosql.user.service.UserService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController implements UsersApi {

  private final UserService userService;

  @Override
  public ResponseEntity<UserResponse> createUser(UserRequest userRequest) {
    UserResponse userResponse = userService.createUser(userRequest);
    return ResponseEntity.created(
            UriUtil.buildUri(UsersApi.PATH_GET_USER_BY_ID, userResponse.getId()))
        .body(userResponse);
  }

  @Override
  public ResponseEntity<Void> deleteUser(UUID userId) {
    userService.deleteUser(userId);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<UserResponse> getUserById(UUID userId) {
    return ResponseEntity.ok(userService.getUserById(userId));
  }

  @Override
  public ResponseEntity<List<UserResponse>> getUsers() {
    return ResponseEntity.ok(userService.getUsers());
  }
}
