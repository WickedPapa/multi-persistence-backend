package it.montano.sqlvsnosql.user.service;

import it.montano.sqlvsnosql.common.exeption.ResourceNotFoundException;
import it.montano.sqlvsnosql.common.mapper.UserMapper;
import it.montano.sqlvsnosql.dto.UserRequest;
import it.montano.sqlvsnosql.dto.UserResponse;
import it.montano.sqlvsnosql.user.model.UserDocument;
import it.montano.sqlvsnosql.user.repository.UserMongoRepository;
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
@ConditionalOnProperty(prefix = "app", name = "datasource", havingValue = "MONGODB")
public class UserMongoService implements UserService {

  private final UserMongoRepository repo;
  private final UserMapper mapper;

  @CacheEvict(value = "users", allEntries = true)
  @Override
  public @NonNull UserResponse createUser(@NonNull UserRequest request) {
    UserDocument doc = mapper.toDocument(request);
    return mapper.toResponse(repo.save(doc));
  }

  @CacheEvict(value = "users", key = "#userId")
  @Override
  public void deleteUser(@NonNull UUID userId) {
    repo.deleteById(userId);
  }

  @Cacheable(value = "users", key = "#userId")
  @Override
  public @NonNull UserResponse getUserById(@NonNull UUID userId) {
    return repo.findById(userId)
        .map(mapper::toResponse)
        .orElseThrow(() -> new ResourceNotFoundException(userId.toString()));
  }

  @Override
  public @NonNull List<UserResponse> getUsers() {
    return repo.findAll().stream().map(mapper::toResponse).toList();
  }
}
