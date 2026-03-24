package it.montano.multipersistencebackend.product.repository;

import it.montano.multipersistencebackend.product.model.ProductDocument;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductMongoRepository extends MongoRepository<ProductDocument, UUID> {}
