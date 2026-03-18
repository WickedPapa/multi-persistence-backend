package it.montano.sqlvsnosql.product.repository;

import it.montano.sqlvsnosql.product.model.ProductDocument;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductMongoRepository extends MongoRepository<ProductDocument, UUID> {}
