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

  @EntityGraph(attributePaths = {"user", "items"})
  @Query("SELECT o FROM OrderEntity o")
  @NonNull
  List<OrderEntity> findAllWithItems();

  @Override
  @EntityGraph(attributePaths = {"user", "items"})
  @NonNull
  Optional<OrderEntity> findById(@NonNull UUID uuid);

  @EntityGraph(attributePaths = {"user", "items"})
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
        o.user.id,
        o.user.firstName,
        o.user.lastName,
        o.user.email,
        SUM(o.total)
    )
    FROM OrderEntity o
    GROUP BY o.user.id, o.user.firstName, o.user.lastName, o.user.email
  """)
  @NonNull
  List<TotalSpentPerUserResponse> getTotalSpentPerUser();

  /**
   * Returns products ordered by the quantity sold.
   *
   * <p>For this demo, Postgres resolves product metadata through a join with current
   * {@code ProductEntity} rows. If a product is removed from the catalog, historical quantities
   * linked to that product are not returned by this query.
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
