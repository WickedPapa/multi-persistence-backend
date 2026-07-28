package it.montano.multipersistencebackend.config.logging;

import it.montano.multipersistencebackend.common.constant.Datasources;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DatasourceLogger {

  private final Environment environment;

  @PostConstruct
  public void logSelectedDatasource() {
    log.info("Startup datasource selected: {}", environment.getProperty(Datasources.PROPERTY_KEY));
  }
}
