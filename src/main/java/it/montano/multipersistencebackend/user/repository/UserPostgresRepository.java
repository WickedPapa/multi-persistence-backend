package it.montano.multipersistencebackend.user.repository;

import it.montano.multipersistencebackend.common.annotation.ConditionalOnDatasource;
import it.montano.multipersistencebackend.common.constant.Datasources;
import it.montano.multipersistencebackend.user.model.UserEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

@ConditionalOnDatasource(Datasources.POSTGRES)
public interface UserPostgresRepository extends JpaRepository<UserEntity, UUID> {}
