package it.montano.multipersistencebackend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {"app.datasource=MONGODB"})
class MongoTest extends AbstractIntegrationTest {

  @Test
  void contextLoads() {}
}
