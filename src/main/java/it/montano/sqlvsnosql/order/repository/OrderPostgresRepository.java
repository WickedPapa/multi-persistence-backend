package it.montano.sqlvsnosql.order.repository;

import it.montano.sqlvsnosql.order.model.OrderEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderPostgresRepository extends JpaRepository<OrderEntity, UUID> {
  List<OrderEntity> findByUserId(UUID userId);
}
