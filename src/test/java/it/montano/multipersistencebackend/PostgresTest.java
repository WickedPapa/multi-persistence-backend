package it.montano.multipersistencebackend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {"app.datasource=POSTGRES"})
class PostgresTest extends AbstractIntegrationTest {

  @Test
  void contextLoads() {}
}
