package it.montano.sqlvsnosql.common.util;

import java.net.URI;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.util.UriComponentsBuilder;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UriUtil {

  public static URI buildUri(String uriTemplate, Object... uriVariables) {
    return UriComponentsBuilder.fromPath(uriTemplate).buildAndExpand(uriVariables).toUri();
  }
}
