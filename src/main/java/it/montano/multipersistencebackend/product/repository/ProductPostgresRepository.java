package it.montano.multipersistencebackend.product.repository;

import it.montano.multipersistencebackend.common.annotation.ConditionalOnDatasource;
import it.montano.multipersistencebackend.common.constant.Datasources;
import it.montano.multipersistencebackend.product.model.ProductEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

@ConditionalOnDatasource(Datasources.POSTGRES)
public interface ProductPostgresRepository extends JpaRepository<ProductEntity, UUID> {}
