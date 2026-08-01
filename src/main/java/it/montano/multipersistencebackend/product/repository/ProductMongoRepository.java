package it.montano.multipersistencebackend.product.repository;

import it.montano.multipersistencebackend.common.annotation.ConditionalOnDatasource;
import it.montano.multipersistencebackend.common.constant.Datasources;
import it.montano.multipersistencebackend.product.model.ProductDocument;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;

@ConditionalOnDatasource(Datasources.MONGO)
public interface ProductMongoRepository extends MongoRepository<ProductDocument, UUID> {}
