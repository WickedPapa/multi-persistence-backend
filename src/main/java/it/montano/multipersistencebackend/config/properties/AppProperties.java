package it.montano.multipersistencebackend.config.properties;

import it.montano.multipersistencebackend.common.constant.Datasources;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = Datasources.PROPERTY_PREFIX)
public record AppProperties(String datasource) {}
