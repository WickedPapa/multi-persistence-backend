package it.montano.multipersistencebackend.config.mongock;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;

/**
 * Initial MongoDB migration — equivalent to Flyway's V1__init.sql for the PostgreSQL backend.
 *
 * <p>Creates all indexes that enforce the same uniqueness and query-performance constraints that
 * the relational schema expresses via PRIMARY KEY and UNIQUE constraints:
 *
 * <ul>
 *   <li>{@code users.email} — unique (mirrors the UNIQUE constraint in the SQL schema)
 *   <li>{@code products.name} — unique (mirrors the UNIQUE constraint in the SQL schema)
 *   <li>{@code orders.user.userId} — non-unique, supports efficient {@code findByUserId} queries
 * </ul>
 *
 * <p>Mongock stores executed changesets in the {@code mongockChangeLog} collection, providing the
 * same idempotent, versioned migration semantics that Flyway provides for PostgreSQL.
 */
@ChangeUnit(id = "v1-init-indexes", order = "001", author = "system")
public class DatabaseMigrationV1 {

  @Execution
  public void execution(MongoTemplate mongoTemplate) {
    mongoTemplate
        .indexOps("users")
        .createIndex(new Index("email", Sort.Direction.ASC).unique().named("users_email_unique"));

    mongoTemplate
        .indexOps("products")
        .createIndex(new Index("name", Sort.Direction.ASC).unique().named("products_name_unique"));

    mongoTemplate
        .indexOps("orders")
        .createIndex(new Index("user.userId", Sort.Direction.ASC).named("orders_user_id_idx"));
  }

  @RollbackExecution
  public void rollback(MongoTemplate mongoTemplate) {
    mongoTemplate.indexOps("users").dropIndex("users_email_unique");
    mongoTemplate.indexOps("products").dropIndex("products_name_unique");
    mongoTemplate.indexOps("orders").dropIndex("orders_user_id_idx");
  }
}
