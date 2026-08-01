package it.montano.multipersistencebackend.user.repository;

import it.montano.multipersistencebackend.common.annotation.ConditionalOnDatasource;
import it.montano.multipersistencebackend.common.constant.Datasources;
import it.montano.multipersistencebackend.user.model.UserDocument;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;

@ConditionalOnDatasource(Datasources.MONGO)
public interface UserMongoRepository extends MongoRepository<UserDocument, UUID> {}
