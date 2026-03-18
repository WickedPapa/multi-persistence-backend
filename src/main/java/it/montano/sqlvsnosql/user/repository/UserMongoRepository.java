package it.montano.sqlvsnosql.user.repository;

import it.montano.sqlvsnosql.user.model.UserDocument;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserMongoRepository extends MongoRepository<UserDocument, UUID> {}
