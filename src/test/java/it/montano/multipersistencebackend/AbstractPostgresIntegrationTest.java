package it.montano.multipersistencebackend;

import it.montano.multipersistencebackend.common.constant.Datasources;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class AbstractPostgresIntegrationTest {

  protected static final String PROPERTY_KEY_EQUALS = Datasources.PROPERTY_KEY + "=";
  private static final String DISABLED_AUTOCONFIGURATIONS =
      "org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration,"
          + "org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration,"
          + "org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration";

  @Container
  static PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>("postgres:15")
          .withDatabaseName("multi_persistence_backend_postgres_db")
          .withUsername("postgres-admin")
          .withPassword("postgres-psw");

  @DynamicPropertySource
  static void configure(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
    registry.add("mongock.enabled", () -> "false");
    registry.add("spring.autoconfigure.exclude", () -> DISABLED_AUTOCONFIGURATIONS);
  }
}
