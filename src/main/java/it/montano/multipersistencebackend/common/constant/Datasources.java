package it.montano.multipersistencebackend.common.constant;

import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Datasources {

  public static final String PROPERTY_PREFIX = "app";
  public static final String PROPERTY_NAME = "datasource";
  public static final String PROPERTY_KEY = PROPERTY_PREFIX + "." + PROPERTY_NAME;
  public static final String POSTGRES = "postgres";
  public static final String MONGO = "mongo";
  public static final List<String> ALLOWED_DATASOURCES = List.of(POSTGRES, MONGO);
}
