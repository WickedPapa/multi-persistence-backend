package it.montano.multipersistencebackend;

import it.montano.multipersistencebackend.common.constant.Datasources;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class AbstractIntegrationTest {

  protected static final String PROPERTY_KEY_EQUALS = Datasources.PROPERTY_KEY + "=";

  @Container
  static PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>("postgres:15")
          .withDatabaseName("multi_persistence_backend_postgres_db")
          .withUsername("postgres-admin")
          .withPassword("postgres-psw");

  @Container static MongoDBContainer mongo = new MongoDBContainer("mongo:7");

  @DynamicPropertySource
  static void configure(DynamicPropertyRegistry registry) {

    // Postgres
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);

    // Mongo
    registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
  }
}
