package it.montano.multipersistencebackend.config.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CaffeineCacheConfig {

  /**
   * Configures the shared cache manager with standard caches and eviction policies.
   *
   * @return configured {@link CacheManager}
   */
  @Bean
  public CacheManager cacheManager() {

    CaffeineCacheManager manager = new CaffeineCacheManager();

    manager.setCaffeine(
        Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(5))
            .maximumSize(5_000)
            .recordStats());

    manager.registerCustomCache(
        "users",
        Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(30))
            .maximumSize(10_000)
            .recordStats()
            .build());

    manager.registerCustomCache(
        "products",
        Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofHours(1))
            .maximumSize(10_000)
            .recordStats()
            .build());

    manager.registerCustomCache(
        "orders",
        Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(10))
            .maximumSize(10_000)
            .recordStats()
            .build());

    manager.registerCustomCache(
        "orders-by-user",
        Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(5))
            .maximumSize(5_000)
            .recordStats()
            .build());

    return manager;
  }
}
