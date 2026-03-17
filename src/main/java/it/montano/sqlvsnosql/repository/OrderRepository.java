package it.montano.sqlvsnosql.repository;

import it.montano.sqlvsnosql.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, String> {}
