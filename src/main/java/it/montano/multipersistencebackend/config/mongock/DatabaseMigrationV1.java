package it.montano.multipersistencebackend.config.mongock;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bson.Document;
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
 *   <li>collection-level {@code $jsonSchema} validators to make Mongo constraints explicit
 * </ul>
 *
 * <p>Mongock stores executed changesets in the {@code mongockChangeLog} collection, providing the
 * same idempotent, versioned migration semantics that Flyway provides for PostgreSQL.
 */
@ChangeUnit(id = "v1-init-indexes", order = "001", author = "system")
public class DatabaseMigrationV1 {

  @Execution
  public void execution(MongoTemplate mongoTemplate) {
    applyJsonSchemaValidators(mongoTemplate);

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

  private void applyJsonSchemaValidators(MongoTemplate mongoTemplate) {
    Set<String> collections = new HashSet<>();
    mongoTemplate.getDb().listCollectionNames().into(collections);

    applyValidator(mongoTemplate, collections, "users", usersSchema());
    applyValidator(mongoTemplate, collections, "products", productsSchema());
    applyValidator(mongoTemplate, collections, "orders", ordersSchema());
  }

  private void applyValidator(
      MongoTemplate mongoTemplate, Set<String> collections, String collection, Document schema) {
    Document validator = new Document("$jsonSchema", schema);
    if (collections.contains(collection)) {
      mongoTemplate
          .getDb()
          .runCommand(
              new Document("collMod", collection)
                  .append("validator", validator)
                  .append("validationLevel", "strict"));
      return;
    }

    mongoTemplate
        .getDb()
        .runCommand(
            new Document("create", collection)
                .append("validator", validator)
                .append("validationLevel", "strict"));
  }

  private Document usersSchema() {
    return new Document("bsonType", "object")
        .append("required", List.of("_id", "firstName", "lastName", "email"))
        .append(
            "properties",
            new Document("_id", new Document("bsonType", "binData"))
                .append("firstName", new Document("bsonType", "string"))
                .append("lastName", new Document("bsonType", "string"))
                .append("email", new Document("bsonType", "string")));
  }

  private Document productsSchema() {
    return new Document("bsonType", "object")
        .append("required", List.of("_id", "name", "price"))
        .append(
            "properties",
            new Document("_id", new Document("bsonType", "binData"))
                .append("name", new Document("bsonType", "string"))
                .append("price", new Document("bsonType", "decimal")));
  }

  private Document ordersSchema() {
    return new Document("bsonType", "object")
        .append("required", List.of("_id", "user", "items", "total"))
        .append(
            "properties",
            new Document("_id", new Document("bsonType", "binData"))
                .append(
                    "user",
                    new Document("bsonType", "object")
                        .append("required", List.of("userId", "firstName", "lastName", "email"))
                        .append(
                            "properties",
                            new Document("userId", new Document("bsonType", "binData"))
                                .append("firstName", new Document("bsonType", "string"))
                                .append("lastName", new Document("bsonType", "string"))
                                .append("email", new Document("bsonType", "string"))))
                .append(
                    "items",
                    new Document("bsonType", "array")
                        .append(
                            "items",
                            new Document("bsonType", "object")
                                .append("required", List.of("productEmbedded", "quantity"))
                                .append(
                                    "properties",
                                    new Document(
                                            "productEmbedded",
                                            new Document("bsonType", "object")
                                                .append(
                                                    "required",
                                                    List.of("productId", "name", "price"))
                                                .append(
                                                    "properties",
                                                    new Document(
                                                            "productId",
                                                            new Document("bsonType", "binData"))
                                                        .append(
                                                            "name",
                                                            new Document("bsonType", "string"))
                                                        .append(
                                                            "price",
                                                            new Document("bsonType", "decimal"))))
                                        .append(
                                            "quantity",
                                            new Document("bsonType", List.of("int", "long"))))))
                .append("total", new Document("bsonType", "decimal")));
  }

  @RollbackExecution
  public void rollback(MongoTemplate mongoTemplate) {
    mongoTemplate.indexOps("users").dropIndex("users_email_unique");
    mongoTemplate.indexOps("products").dropIndex("products_name_unique");
    mongoTemplate.indexOps("orders").dropIndex("orders_user_id_idx");
  }
}
