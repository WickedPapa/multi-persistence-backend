package it.montano.multipersistencebackend.user.repository;

import it.montano.multipersistencebackend.user.model.UserDocument;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserMongoRepository extends MongoRepository<UserDocument, UUID> {}
