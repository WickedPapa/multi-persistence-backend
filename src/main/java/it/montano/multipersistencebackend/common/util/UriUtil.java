package it.montano.multipersistencebackend.common.util;

import java.net.URI;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.util.UriComponentsBuilder;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UriUtil {

  /**
   * Builds a URI from the provided template and variables.
   *
   * @param uriTemplate URI template string
   * @param uriVariables template variables to expand
   * @return resolved URI instance
   */
  public static URI buildUri(String uriTemplate, Object... uriVariables) {
    return UriComponentsBuilder.fromPath(uriTemplate).buildAndExpand(uriVariables).toUri();
  }
}
