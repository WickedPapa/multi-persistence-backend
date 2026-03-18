package it.montano.sqlvsnosql.product.repository;

import it.montano.sqlvsnosql.product.model.ProductEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductPostgresRepository extends JpaRepository<ProductEntity, UUID> {}
