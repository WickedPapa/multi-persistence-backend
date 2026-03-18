package it.montano.sqlvsnosql.user.service;

import it.montano.sqlvsnosql.dto.UserRequest;
import it.montano.sqlvsnosql.dto.UserResponse;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;

public interface UserService {

  @NonNull
  UserResponse createUser(@NonNull UserRequest userRequest);

  void deleteUser(@NonNull UUID userId);

  @NonNull
  UserResponse getUserById(@NonNull UUID userId);

  @NonNull
  List<UserResponse> getUsers();
}
