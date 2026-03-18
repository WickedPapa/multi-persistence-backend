package it.montano.sqlvsnosql.user.repository;

import it.montano.sqlvsnosql.user.model.UserEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPostgresRepository extends JpaRepository<UserEntity, UUID> {}
