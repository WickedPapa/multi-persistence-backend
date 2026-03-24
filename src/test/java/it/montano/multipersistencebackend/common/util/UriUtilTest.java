package it.montano.multipersistencebackend.common.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class UriUtilTest {

  @Test
  void shouldBuildUriUsingTemplateVariables() {
    UUID id = UUID.randomUUID();

    URI result = UriUtil.buildUri("/resource/{id}", id);

    assertThat(result).hasToString("/resource/" + id);
  }
}
