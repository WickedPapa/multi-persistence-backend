package it.montano.multipersistencebackend.order.repository;

import it.montano.multipersistencebackend.dto.MostSoldProductResponse;
import it.montano.multipersistencebackend.dto.TotalSpentPerUserResponse;
import it.montano.multipersistencebackend.order.model.OrderEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderPostgresRepository extends JpaRepository<OrderEntity, UUID> {

  @EntityGraph(attributePaths = {"items"})
  @Query("SELECT o FROM OrderEntity o")
  @NonNull
  List<OrderEntity> findAllWithItems();

  @Override
  @EntityGraph(attributePaths = {"items"})
  @NonNull
  Optional<OrderEntity> findById(@NonNull UUID uuid);

  @EntityGraph(attributePaths = {"items"})
  @NonNull
  List<OrderEntity> findByUserId(@NonNull UUID userId);

  /**
   * Aggregates spending totals grouped by user via JPQL.
   *
   * @return list containing the total spent per user
   */
  @Query(
      """
    SELECT new it.montano.multipersistencebackend.dto.TotalSpentPerUserResponse(
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
  @NonNull
  List<TotalSpentPerUserResponse> getTotalSpentPerUser();

  /**
   * Returns products ordered by the quantity sold.
   *
   * @return products with cumulative quantities
   */
  @Query(
      """
    SELECT new it.montano.multipersistencebackend.dto.MostSoldProductResponse(
        p.id,
        p.name,
        SUM(oi.quantity)
    )
    FROM OrderItemEntity oi, ProductEntity p
    WHERE oi.productId = p.id
    GROUP BY p.id, p.name
    ORDER BY SUM(oi.quantity) DESC
  """)
  @NonNull
  List<MostSoldProductResponse> getMostSoldProduct();
}
