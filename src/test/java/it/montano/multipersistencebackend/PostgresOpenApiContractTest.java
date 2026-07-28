package it.montano.multipersistencebackend;

import it.montano.multipersistencebackend.common.constant.Datasources;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@SpringBootTest(
    properties = {AbstractPostgresIntegrationTest.PROPERTY_KEY_EQUALS + Datasources.POSTGRES})
class PostgresOpenApiContractTest extends AbstractPostgresIntegrationTest {

  @Autowired
  @Qualifier("requestMappingHandlerMapping")
  RequestMappingHandlerMapping handlerMapping;

  @Test
  void shouldExposeAllOperationsDeclaredInOpenApiSpec() {
    OpenApiContractSupport.assertOpenApiOperationsAreExposed(handlerMapping);
  }
}
