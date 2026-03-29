package it.montano.multipersistencebackend;

import static org.assertj.core.api.Assertions.assertThat;

import it.montano.multipersistencebackend.common.constant.Datasources;
import it.montano.multipersistencebackend.order.service.OrderPostgresService;
import it.montano.multipersistencebackend.order.service.OrderService;
import it.montano.multipersistencebackend.product.service.ProductPostgresService;
import it.montano.multipersistencebackend.product.service.ProductService;
import it.montano.multipersistencebackend.user.service.UserPostgresService;
import it.montano.multipersistencebackend.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest(properties = {AbstractIntegrationTest.PROPERTY_KEY_EQUALS + Datasources.POSTGRES})
class PostgresTest extends AbstractIntegrationTest {

  @Autowired ApplicationContext context;

  @Test
  void contextLoads() {
    assertThat(context.getBean(UserService.class))
        .isNotNull()
        .isInstanceOf(UserPostgresService.class);
    assertThat(context.getBean(ProductService.class))
        .isNotNull()
        .isInstanceOf(ProductPostgresService.class);
    assertThat(context.getBean(OrderService.class))
        .isNotNull()
        .isInstanceOf(OrderPostgresService.class);
  }
}
