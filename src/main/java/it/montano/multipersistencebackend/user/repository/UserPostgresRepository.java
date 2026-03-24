package it.montano.multipersistencebackend.user.repository;

import it.montano.multipersistencebackend.user.model.UserEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPostgresRepository extends JpaRepository<UserEntity, UUID> {}
