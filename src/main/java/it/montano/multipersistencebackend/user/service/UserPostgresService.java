package it.montano.multipersistencebackend.user.service;

import it.montano.multipersistencebackend.common.mapper.UserMapper;
import it.montano.multipersistencebackend.config.exeption.ResourceNotFoundException;
import it.montano.multipersistencebackend.dto.UserRequest;
import it.montano.multipersistencebackend.dto.UserResponse;
import it.montano.multipersistencebackend.user.model.UserEntity;
import it.montano.multipersistencebackend.user.repository.UserPostgresRepository;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app", name = "datasource", havingValue = "POSTGRES")
public class UserPostgresService implements UserService {

  private final UserPostgresRepository repo;
  private final UserMapper mapper;

  /**
   * Creates a user entity in Postgres.
   *
   * @param request API user payload
   * @return persisted user response
   */
  @CacheEvict(value = "users", allEntries = true)
  @Override
  public @NonNull UserResponse createUser(@NonNull UserRequest request) {
    UserEntity entity = mapper.toEntity(request);
    return mapper.toResponse(repo.save(entity));
  }

  /**
   * Deletes a user and clears its cache entry.
   *
   * @param userId identifier of the user
   */
  @CacheEvict(value = "users", key = "#userId")
  @Override
  public void deleteUser(@NonNull UUID userId) {
    repo.deleteById(userId);
  }

  /**
   * Retrieves a user by id with caching.
   *
   * @param userId identifier of the user
   * @return found user response
   */
  @Cacheable(value = "users", key = "#userId")
  @Override
  public @NonNull UserResponse getUserById(@NonNull UUID userId) {
    return repo.findById(userId)
        .map(mapper::toResponse)
        .orElseThrow(() -> new ResourceNotFoundException(userId.toString()));
  }

  /**
   * Lists every user persisted in Postgres.
   *
   * @return list of user responses
   */
  @Override
  public @NonNull List<UserResponse> getUsers() {
    return repo.findAll().stream().map(mapper::toResponse).toList();
  }
}
