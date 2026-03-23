package it.montano.sqlvsnosql.user.service;

import it.montano.sqlvsnosql.dto.UserRequest;
import it.montano.sqlvsnosql.dto.UserResponse;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;

public interface UserService {

  /**
   * Creates a new user from the given payload.
   *
   * @param userRequest API user payload
   * @return created user response
   */
  @NonNull
  UserResponse createUser(@NonNull UserRequest userRequest);

  /**
   * Deletes a user by identifier.
   *
   * @param userId identifier of the user
   */
  void deleteUser(@NonNull UUID userId);

  /**
   * Retrieves a single user by id.
   *
   * @param userId identifier of the user
   * @return found user response
   */
  @NonNull
  UserResponse getUserById(@NonNull UUID userId);

  /**
   * Lists all users in the system.
   *
   * @return list of user responses
   */
  @NonNull
  List<UserResponse> getUsers();
}
