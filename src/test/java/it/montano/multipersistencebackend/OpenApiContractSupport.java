package it.montano.multipersistencebackend;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStream;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.yaml.snakeyaml.Yaml;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class OpenApiContractSupport {

  private static final List<String> HTTP_METHODS =
      List.of("get", "post", "put", "delete", "patch", "head", "options", "trace");
  private static final String OPENAPI_SPEC_PATH = "openapi/api.yaml";

  static void assertOpenApiOperationsAreExposed(RequestMappingHandlerMapping handlerMapping) {
    Set<String> specOperations = readOperationsFromSpec();
    Set<String> runtimeOperations = readOperationsFromRuntime(handlerMapping);

    assertThat(runtimeOperations).containsAll(specOperations);
  }

  @SuppressWarnings("unchecked")
  private static Set<String> readOperationsFromSpec() {
    try (InputStream inputStream =
        OpenApiContractSupport.class.getClassLoader().getResourceAsStream(OPENAPI_SPEC_PATH)) {
      assertThat(inputStream).as("OpenAPI spec not found: %s", OPENAPI_SPEC_PATH).isNotNull();

      Map<String, Object> root = new Yaml().load(inputStream);
      Map<String, Object> paths = (Map<String, Object>) root.get("paths");
      Set<String> operations = new LinkedHashSet<>();

      for (Map.Entry<String, Object> pathEntry : paths.entrySet()) {
        String path = pathEntry.getKey();
        Map<String, Object> methods = (Map<String, Object>) pathEntry.getValue();
        for (String method : methods.keySet()) {
          if (HTTP_METHODS.contains(method.toLowerCase())) {
            operations.add(method.toUpperCase() + " " + path);
          }
        }
      }

      return operations;
    } catch (Exception ex) {
      throw new IllegalStateException("Unable to parse OpenAPI contract: " + OPENAPI_SPEC_PATH, ex);
    }
  }

  private static Set<String> readOperationsFromRuntime(RequestMappingHandlerMapping handlerMapping) {
    Set<String> operations = new LinkedHashSet<>();
    Set<String> specPaths = readPathsFromSpec();

    handlerMapping
        .getHandlerMethods()
        .forEach(
            (requestMappingInfo, handlerMethod) -> {
              Set<String> paths = extractPaths(requestMappingInfo);
              Set<RequestMethod> methods = requestMappingInfo.getMethodsCondition().getMethods();
              if (methods.isEmpty()) {
                return;
              }
              for (String path : paths) {
                if (!specPaths.contains(path)) {
                  continue;
                }
                for (RequestMethod method : methods) {
                  operations.add(method.name() + " " + path);
                }
              }
            });

    return operations;
  }

  @SuppressWarnings("unchecked")
  private static Set<String> readPathsFromSpec() {
    try (InputStream inputStream =
        OpenApiContractSupport.class.getClassLoader().getResourceAsStream(OPENAPI_SPEC_PATH)) {
      assertThat(inputStream).as("OpenAPI spec not found: %s", OPENAPI_SPEC_PATH).isNotNull();

      Map<String, Object> root = new Yaml().load(inputStream);
      Map<String, Object> paths = (Map<String, Object>) root.get("paths");
      return new LinkedHashSet<>(paths.keySet());
    } catch (Exception ex) {
      throw new IllegalStateException("Unable to parse OpenAPI paths: " + OPENAPI_SPEC_PATH, ex);
    }
  }

  private static Set<String> extractPaths(RequestMappingInfo requestMappingInfo) {
    if (requestMappingInfo.getPathPatternsCondition() != null) {
      return requestMappingInfo.getPathPatternsCondition().getPatternValues();
    }
    if (requestMappingInfo.getPatternsCondition() != null) {
      return requestMappingInfo.getPatternsCondition().getPatterns();
    }
    return Set.of();
  }
}
