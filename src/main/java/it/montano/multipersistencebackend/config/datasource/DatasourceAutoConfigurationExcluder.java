package it.montano.multipersistencebackend.config.datasource;

import it.montano.multipersistencebackend.common.constant.Datasources;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

/**
 * Excludes irrelevant persistence autoconfiguration based on the selected datasource.
 *
 * <p>Runs before the Spring application context is created, so the JPA/Flyway stack is never
 * initialized when {@code app.datasource=mongo}, and the MongoDB stack is never initialized when
 * {@code app.datasource=postgres}. This makes the two backends cleanly independent and fails fast
 * for invalid datasource values.
 *
 * <p>Registered via {@code META-INF/spring.factories}.
 */
public class DatasourceAutoConfigurationExcluder implements EnvironmentPostProcessor {

  private static final String MONGO_AUTOCONFIGURATIONS =
      String.join(
          ",",
          "org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration",
          "org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration",
          "org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration");

  private static final String POSTGRES_AUTOCONFIGURATIONS =
      String.join(
          ",",
          "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration",
          "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration",
          "org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration",
          "org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration");

  @Override
  public void postProcessEnvironment(
      ConfigurableEnvironment environment, SpringApplication application) {
    String datasource = environment.getProperty(Datasources.PROPERTY_KEY);

    if (!Datasources.ALLOWED_DATASOURCES.contains(datasource)) {
      throw new IllegalStateException(
          String.format(
              "Missing or invalid %s: '%s'. Allowed values: %s",
              Datasources.PROPERTY_KEY,
              datasource,
              Arrays.toString(Datasources.ALLOWED_DATASOURCES.toArray())));
    }

    Map<String, Object> properties = new HashMap<>();

    if (Datasources.MONGO.equals(datasource)) {
      properties.put("spring.autoconfigure.exclude", POSTGRES_AUTOCONFIGURATIONS);
      properties.put("mongock.enabled", "true");
    } else {
      properties.put("spring.autoconfigure.exclude", MONGO_AUTOCONFIGURATIONS);
      properties.put("mongock.enabled", "false");
    }

    environment
        .getPropertySources()
        .addFirst(new MapPropertySource("datasourceAutoConfigExclusions", properties));
  }
}
