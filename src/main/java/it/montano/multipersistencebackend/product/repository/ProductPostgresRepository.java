package it.montano.multipersistencebackend.product.repository;

import it.montano.multipersistencebackend.product.model.ProductEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductPostgresRepository extends JpaRepository<ProductEntity, UUID> {}
