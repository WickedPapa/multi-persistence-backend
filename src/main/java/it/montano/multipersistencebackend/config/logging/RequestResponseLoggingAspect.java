package it.montano.multipersistencebackend.config.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
public class RequestResponseLoggingAspect {

  /**
   * Wraps REST controllers to log inbound requests and outbound responses.
   *
   * @param joinPoint intercepted controller invocation
   * @return controller result
   * @throws Throwable rethrows underlying exception
   */
  @Around("within(@org.springframework.web.bind.annotation.RestController *)")
  public Object logAroundController(ProceedingJoinPoint joinPoint) throws Throwable {
    ServletRequestAttributes attributes = currentRequest();
    if (attributes == null) {
      return joinPoint.proceed();
    }

    HttpServletRequest request = attributes.getRequest();
    HttpServletResponse response = attributes.getResponse();
    String handler = joinPoint.getSignature().toShortString();
    String method = request.getMethod();
    String uri = buildUri(request);
    String client = resolveClientIp(request);
    String userAgent = Optional.ofNullable(request.getHeader("User-Agent")).orElse("unknown");
    String requestId = UUID.randomUUID().toString();
    long start = System.nanoTime();

    MDC.put("requestId", requestId);
    log.info(
        "Request intercepted:\n--------------------------------------------\nINCOMING REQUEST:\nrequestId = {}\nmethod = {}\nuri = {}\nhandler = {}\nclient = {}\nuserAgent = {}\n--------------------------------------------",
        requestId,
        method,
        uri,
        handler,
        client,
        userAgent);

    try {
      Object result = joinPoint.proceed();
      long durationMs = Duration.ofNanos(System.nanoTime() - start).toMillis();
      log.info(
          "Response intercepted\n--------------------------------------------\nOUTGOING RESPONSE:\nrequestId = {}\nmethod = {}\nuri = {}\nstatus = {}\ndurationMs = {}\nhandler = {}\nclient = {}\n--------------------------------------------",
          requestId,
          method,
          uri,
          response != null ? response.getStatus() : HttpStatus.INTERNAL_SERVER_ERROR.value(),
          durationMs,
          handler,
          client);
      return result;
    } catch (Throwable ex) {
      long durationMs = Duration.ofNanos(System.nanoTime() - start).toMillis();
      log.error(
          "Error intercepted\n--------------------------------------------\nOUTGOING ERROR RESPONSE:\nrequestId = {}\nmethod = {}\nuri = {}\ndurationMs = {}\nhandler = {}\nclient = {}\n--------------------------------------------",
          requestId,
          method,
          uri,
          durationMs,
          handler,
          client);
      throw ex;
    } finally {
      MDC.remove("requestId");
    }
  }

  private ServletRequestAttributes currentRequest() {
    RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
    if (attributes instanceof ServletRequestAttributes servletAttributes) {
      return servletAttributes;
    }
    return null;
  }

  private String buildUri(HttpServletRequest request) {
    String path = request.getRequestURI();
    String queryString = request.getQueryString();
    return StringUtils.hasText(queryString) ? path + "?" + queryString : path;
  }

  private String resolveClientIp(HttpServletRequest request) {
    String ip = request.getHeader("X-Forwarded-For");
    if (!StringUtils.hasText(ip)) {
      ip = request.getHeader("X-Real-IP");
    }
    if (StringUtils.hasText(ip)) {
      return ip.split(",")[0].trim();
    }
    return request.getRemoteAddr();
  }
}
