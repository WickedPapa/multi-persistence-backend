package it.montano.multipersistencebackend.common.annotation;

import it.montano.multipersistencebackend.common.constant.Datasources;
import java.lang.annotation.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.AliasFor;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ConditionalOnProperty(prefix = Datasources.PROPERTY_PREFIX, name = Datasources.PROPERTY_NAME)
public @interface ConditionalOnDatasource {

  @AliasFor(annotation = ConditionalOnProperty.class, attribute = "havingValue")
  String value();
}
