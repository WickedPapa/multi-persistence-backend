package it.montano.multipersistencebackend.user.service;

import it.montano.multipersistencebackend.common.annotation.ConditionalOnDatasource;
import it.montano.multipersistencebackend.common.constant.Datasources;
import it.montano.multipersistencebackend.common.mapper.UserMapper;
import it.montano.multipersistencebackend.config.exeption.ResourceNotFoundException;
import it.montano.multipersistencebackend.dto.UserRequest;
import it.montano.multipersistencebackend.dto.UserResponse;
import it.montano.multipersistencebackend.user.model.UserDocument;
import it.montano.multipersistencebackend.user.repository.UserMongoRepository;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnDatasource(Datasources.MONGO)
public class UserMongoService implements UserService {

  private final UserMongoRepository repo;
  private final UserMapper mapper;

  /**
   * Creates a user document in MongoDB.
   *
   * @param request API user payload
   * @return persisted user response
   */
  @CacheEvict(value = "users", allEntries = true)
  @Override
  public @NonNull UserResponse createUser(@NonNull UserRequest request) {
    UserDocument doc = mapper.toDocument(request);
    return mapper.toResponse(repo.save(doc));
  }

  /**
   * Deletes a user and evicts any cached entry.
   *
   * @param userId identifier of the user
   */
  @CacheEvict(value = "users", key = "#userId")
  @Override
  public void deleteUser(@NonNull UUID userId) {
    repo.deleteById(userId);
  }

  /**
   * Retrieves a user by id leveraging cache.
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
   * Lists every user stored in MongoDB.
   *
   * @return list of user responses
   */
  @Override
  public @NonNull List<UserResponse> getUsers() {
    return repo.findAll().stream().map(mapper::toResponse).toList();
  }
}
