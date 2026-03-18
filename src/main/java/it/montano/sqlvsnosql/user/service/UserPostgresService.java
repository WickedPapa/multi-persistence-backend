package it.montano.sqlvsnosql.user.service;

import it.montano.sqlvsnosql.common.mapper.UserMapper;
import it.montano.sqlvsnosql.dto.UserRequest;
import it.montano.sqlvsnosql.dto.UserResponse;
import it.montano.sqlvsnosql.user.model.UserEntity;
import it.montano.sqlvsnosql.user.repository.UserPostgresRepository;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app", name = "datasource", havingValue = "POSTGRES")
public class UserPostgresService implements UserService {

  private final UserPostgresRepository repo;
  private final UserMapper mapper;

  @Override
  public @NonNull UserResponse createUser(@NonNull UserRequest request) {
    UserEntity entity = mapper.toEntity(request);
    return mapper.toResponse(repo.save(entity));
  }

  @Override
  public void deleteUser(@NonNull UUID userId) {
    repo.deleteById(userId);
  }

  @Override
  public @NonNull UserResponse getUserById(@NonNull UUID userId) {
    return repo.findById(userId).map(mapper::toResponse).orElseThrow();
  }

  @Override
  public @NonNull List<UserResponse> getUsers() {
    return repo.findAll().stream().map(mapper::toResponse).toList();
  }
}
