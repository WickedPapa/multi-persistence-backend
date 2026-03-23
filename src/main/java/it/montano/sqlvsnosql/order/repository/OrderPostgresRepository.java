package it.montano.sqlvsnosql.order.repository;

import it.montano.sqlvsnosql.dto.MostSoldProductResponse;
import it.montano.sqlvsnosql.dto.TotalSpentPerUserResponse;
import it.montano.sqlvsnosql.order.model.OrderEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderPostgresRepository extends JpaRepository<OrderEntity, UUID> {
  List<OrderEntity> findByUserId(UUID userId);

  /**
   * Aggregates spending totals grouped by user via JPQL.
   *
   * @return list containing the total spent per user
   */
  @Query(
      """
    SELECT new it.montano.sqlvsnosql.dto.TotalSpentPerUserResponse(
        u.id,
        u.firstName,
        u.lastName,
        u.email,
        SUM(o.total)
    )
    FROM OrderEntity o, UserEntity u
    WHERE o.userId = u.id
    GROUP BY u.id, u.firstName, u.lastName, u.email
  """)
  List<TotalSpentPerUserResponse> getTotalSpentPerUser();

  /**
   * Returns products ordered by the quantity sold.
   *
   * @return products with cumulative quantities
   */
  @Query(
      """
    SELECT new it.montano.sqlvsnosql.dto.MostSoldProductResponse(
        p.id,
        p.name,
        SUM(oi.quantity)
    )
    FROM OrderItemEntity oi, ProductEntity p
    WHERE oi.productId = p.id
    GROUP BY p.id, p.name
    ORDER BY SUM(oi.quantity) DESC
  """)
  List<MostSoldProductResponse> getMostSoldProduct();
}
