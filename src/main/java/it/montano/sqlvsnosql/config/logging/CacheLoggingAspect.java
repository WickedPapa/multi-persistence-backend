package it.montano.sqlvsnosql.config.logging;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.Ordered;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class CacheLoggingAspect {

  private final CacheManager cacheManager;
  private final ApplicationContext context;
  private final ObjectProvider<KeyGenerator> keyGeneratorProvider;
  private final ParameterNameDiscoverer parameterNameDiscoverer =
      new DefaultParameterNameDiscoverer();
  private final ExpressionParser parser = new SpelExpressionParser();

  /**
   * Logs cache hits and misses for {@link Cacheable} methods.
   *
   * @param joinPoint intercepted invocation
   * @return original method result
   * @throws Throwable propagated target exception
   */
  @Around("@annotation(org.springframework.cache.annotation.Cacheable)")
  public Object logCacheable(ProceedingJoinPoint joinPoint) throws Throwable {

    Method method = getMethod(joinPoint);
    Cacheable cacheable = method.getAnnotation(Cacheable.class);

    if (cacheable == null) {
      return joinPoint.proceed();
    }

    CacheManager manager = selectCacheManager(cacheable.cacheManager());

    Object key = resolveKey(cacheable.key(), cacheable.keyGenerator(), method, joinPoint);

    resolveCacheNames(cacheable.cacheNames(), cacheable.value())
        .forEach(cacheName -> log(manager, cacheName, key, method, CacheOperation.HIT_MISS));
    return joinPoint.proceed();
  }

  /**
   * Logs cache put operations triggered by {@link CachePut}.
   *
   * @param joinPoint intercepted invocation
   * @return original method result
   * @throws Throwable propagated target exception
   */
  @Around("@annotation(org.springframework.cache.annotation.CachePut)")
  public Object logCachePut(ProceedingJoinPoint joinPoint) throws Throwable {

    Method method = getMethod(joinPoint);
    CachePut cachePut = method.getAnnotation(CachePut.class);

    if (cachePut == null) {
      return joinPoint.proceed();
    }

    CacheManager manager = selectCacheManager(cachePut.cacheManager());

    Object key = resolveKey(cachePut.key(), cachePut.keyGenerator(), method, joinPoint);

    Object result = joinPoint.proceed();

    resolveCacheNames(cachePut.cacheNames(), cachePut.value())
        .forEach(cacheName -> log(manager, cacheName, key, method, CacheOperation.PUT));

    return result;
  }

  /**
   * Logs cache evictions triggered by {@link CacheEvict}.
   *
   * @param joinPoint intercepted invocation
   * @return original method result
   * @throws Throwable propagated target exception
   */
  @Around("@annotation(org.springframework.cache.annotation.CacheEvict)")
  public Object logCacheEvict(ProceedingJoinPoint joinPoint) throws Throwable {

    Method method = getMethod(joinPoint);
    CacheEvict cacheEvict = method.getAnnotation(CacheEvict.class);

    if (cacheEvict == null) {
      return joinPoint.proceed();
    }

    CacheManager manager = selectCacheManager(cacheEvict.cacheManager());

    Object key =
        cacheEvict.allEntries()
            ? "ALL ENTRIES"
            : resolveKey(cacheEvict.key(), cacheEvict.keyGenerator(), method, joinPoint);

    if (cacheEvict.beforeInvocation()) {
      resolveCacheNames(cacheEvict.cacheNames(), cacheEvict.value())
          .forEach(cacheName -> log(manager, cacheName, key, method, CacheOperation.EVICT));
    }

    Object result = joinPoint.proceed();

    if (!cacheEvict.beforeInvocation()) {
      resolveCacheNames(cacheEvict.cacheNames(), cacheEvict.value())
          .forEach(cacheName -> log(manager, cacheName, key, method, CacheOperation.EVICT));
    }

    return result;
  }

  private void log(
      CacheManager manager, String cacheName, Object key, Method method, CacheOperation operation) {
    if (key == null) {
      log.warn(
          "Cache {} skip {} log for {} because key resolved to null",
          cacheName,
          operation.name(),
          method);
      return;
    }
    Cache cache = manager.getCache(cacheName);
    if (cache == null) {
      log.warn("Cache {} not found for {}", cacheName, method);
      return;
    }
    CacheOperation finalOperation = operation;
    if (operation == CacheOperation.HIT_MISS) {
      Cache.ValueWrapper wrapper = cache.get(key);
      finalOperation = wrapper != null ? CacheOperation.HIT : CacheOperation.MISS;
    }
    log.info(
        "Cache invoked\n--------------------------------------------\nCACHE|{}|{}|{}|{}\n--------------------------------------------",
        cacheName,
        method.getName(),
        key,
        finalOperation.name());
  }

  private Object resolveKey(
      String expression, String keyGeneratorBeanName, Method method, JoinPoint joinPoint) {
    Object target = joinPoint.getTarget();
    Object[] args = joinPoint.getArgs();
    if (StringUtils.hasText(expression)) {
      try {
        MethodBasedEvaluationContext methodBasedEvaluationContext =
            new MethodBasedEvaluationContext(target, method, args, parameterNameDiscoverer);
        Expression parsed = parser.parseExpression(expression);
        return parsed.getValue(methodBasedEvaluationContext);
      } catch (Exception ex) {
        log.warn(
            "Failed to evaluate cache key expression '{}' for {}: {}",
            expression,
            method,
            ex.getMessage());
        return null;
      }
    }
    KeyGenerator generator = getKeyGenerator(keyGeneratorBeanName);
    try {
      return generator.generate(target, method, args);
    } catch (Exception ex) {
      log.warn(
          "Failed to generate cache key via {} for {}: {}", generator, method, ex.getMessage());
      return null;
    }
  }

  private KeyGenerator getKeyGenerator(String keyGeneratorBeanName) {
    if (StringUtils.hasText(keyGeneratorBeanName)) {
      return context.getBean(keyGeneratorBeanName, KeyGenerator.class);
    }
    KeyGenerator generator = keyGeneratorProvider.getIfAvailable();
    return generator != null ? generator : new SimpleKeyGenerator();
  }

  private Method getMethod(JoinPoint joinPoint) {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();
    return AopUtils.getMostSpecificMethod(method, joinPoint.getTarget().getClass());
  }

  private List<String> resolveCacheNames(String[] cacheNames, String[] values) {
    String[] resolved = cacheNames.length > 0 ? cacheNames : values;
    return Arrays.asList(resolved);
  }

  private CacheManager selectCacheManager(String cacheManagerBeanName) {
    if (StringUtils.hasText(cacheManagerBeanName)) {
      return context.getBean(cacheManagerBeanName, CacheManager.class);
    }
    return cacheManager;
  }

  private enum CacheOperation {
    HIT_MISS,
    HIT,
    MISS,
    EVICT,
    PUT
  }
}
