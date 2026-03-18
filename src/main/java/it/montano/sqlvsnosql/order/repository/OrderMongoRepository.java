package it.montano.sqlvsnosql.order.repository;

import it.montano.sqlvsnosql.order.model.OrderDocument;
import java.util.List;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderMongoRepository extends MongoRepository<OrderDocument, UUID> {
  List<OrderDocument> findByUserId(UUID userId);
}
