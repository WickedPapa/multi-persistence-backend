package it.montano.multipersistencebackend.order.repository;

import it.montano.multipersistencebackend.dto.MostSoldProductResponse;
import it.montano.multipersistencebackend.dto.TotalSpentPerUserResponse;
import it.montano.multipersistencebackend.order.model.OrderDocument;
import java.util.List;
import java.util.UUID;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderMongoRepository extends MongoRepository<OrderDocument, UUID> {
  List<OrderDocument> findByUserUserId(UUID userId);

  /**
   * Aggregates spending totals grouped by user.
   *
   * @return summary with cumulative spent amounts
   */
  @Aggregation(
      pipeline = {
        "{ $group: { "
            + "_id: '$user.userId', "
            + "total: { $sum: '$total' }, "
            + "firstName: { $first: '$user.firstName' }, "
            + "lastName: { $first: '$user.lastName' }, "
            + "email: { $first: '$user.email' } "
            + "} }",
        "{ $project: { "
            + "_id: 0, "
            + "userId: '$_id', "
            + "totalSpent: '$total', "
            + "firstName: 1, "
            + "lastName: 1, "
            + "email: 1 "
            + "} }"
      })
  List<TotalSpentPerUserResponse> getTotalSpentPerUser();

  /**
   * Aggregates the most sold product with total quantity.
   *
   * @return ordered list of products by quantity sold
   */
  @Aggregation(
      pipeline = {
        "{ $unwind: '$items' }",
        "{ $group: { _id: '$items.productEmbedded._id', name: { $first: '$items.productEmbedded.name' }, totalQuantity: { $sum: '$items.quantity' } } }",
        "{ $sort: { totalQuantity: -1 } }",
        "{ $project: { _id: 0, productId: '$_id', name: 1, totalQuantity: 1 } }"
      })
  List<MostSoldProductResponse> getMostSoldProduct();
}
