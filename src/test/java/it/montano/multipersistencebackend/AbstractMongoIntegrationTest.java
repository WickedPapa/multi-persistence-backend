package it.montano.multipersistencebackend;

import it.montano.multipersistencebackend.common.constant.Datasources;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class AbstractMongoIntegrationTest {

  protected static final String PROPERTY_KEY_EQUALS = Datasources.PROPERTY_KEY + "=";

  private static final String DISABLED_AUTOCONFIGURATIONS =
      "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,"
          + "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,"
          + "org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration";

  @Container static MongoDBContainer mongo = new MongoDBContainer("mongo:7");

  @DynamicPropertySource
  static void configure(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
    registry.add("spring.autoconfigure.exclude", () -> DISABLED_AUTOCONFIGURATIONS);
  }
}
